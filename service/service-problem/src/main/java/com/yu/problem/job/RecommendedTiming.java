package com.yu.problem.job;

import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.feign.UserFeignClient;
import com.yu.model.entity.Question;
import com.yu.model.entity.QuestionSubmit;
import com.yu.model.entity.User;
import com.yu.model.vo.ProblemCompleteCountVo;
import com.yu.problem.mapper.QuestionMapper;
import com.yu.problem.mapper.QuestionSubmitMapper;
import com.yu.problem.utils.RecommendUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.yu.common.constant.QuestionConstant.RECOMMEND_NUM;


/**
 * 定时任务
 *
 * @author yu
 */
@Component
@Slf4j
public class RecommendedTiming {

    @Resource
    private QuestionMapper problemMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private QuestionSubmitMapper problemSubmitMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    private static final String REDISSON_LOCK = "recommended-lock";

    private static final String RECOMMENDED_LIST = "recommended-list";

    private static final String RECOMMENDED_LIST_BY_USER = "recommended-list-by-user:";


    /**
     * 在每天凌晨三点运行，根据题目通过率和难度，对新用户进行推送题目
     */
    @Scheduled(cron = "0 0 1 ? * 7")
    public void updateRecommendedList() {
        List<ProblemCompleteCountVo> list = problemMapper.getProblemCompleteCount(2);

        //todo 应该利用用户协同算法，这里只是根据题目通过率和难度，对新用户进行推送题目，应该再考虑用户之间的相似度
        Map<Long, Double> map = list.stream()
                .collect(Collectors.toMap(
                        ProblemCompleteCountVo::getProblemId,
                        vo -> {
                            double count = 0.0;
                            if ("简单".equals(vo.getDifficulty())) {
                                count = 1.0 * vo.getCompletionCount();
                            } else if ("中等".equals(vo.getDifficulty())) {
                                count = 0.8 * vo.getCompletionCount();
                            } else {
                                count = 0.6 * vo.getCompletionCount();
                            }
                            return count;
                        },
                        Double::sum
                ));

        //对分数进行升序排序
        List<Long> list1 = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());


        List<Question> res = new ArrayList<>();
        for (Long l : list1) {
            Question problem = problemMapper.selectById(l);
            res.add(problem);
        }

        List<Question> problems = problemMapper.selectList(null);
        res.addAll(problems);

        res = res.stream().distinct().limit(50).collect(Collectors.toList());


        //获取锁
        RLock lock = redissonClient.getLock(REDISSON_LOCK);
        try {

            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    //业务代码
                    String str = JSONUtil.toJsonStr(res);
                    stringRedisTemplate.opsForValue().set(RECOMMENDED_LIST, str);

                } catch (Exception e) {
                    log.info("加锁成功，但是出现错误{}", e);
                } finally {
                    lock.unlock();
                }
            }

        } catch (Exception e) {
            log.info("加锁出现错误{}", e);
        }

    }


    /**
     * 定时任务，缓存每一个用户的推荐题目
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateUserRecommendedList() {
        List<User> userList = userFeignClient.getAllUser();
        for (User user : userList) {
            String username = user.getUserName();
            String key = RECOMMENDED_LIST_BY_USER + username;

            Long uid = user.getId();
            List<Question> list = updateUserRecommendedList(uid);
            String json = JSONUtil.toJsonStr(list);

            stringRedisTemplate.opsForValue().set(key, json);
        }
    }

    /**
     * 获得单个用户的推荐列表
     *
     * @param uid
     * @return
     */
    public List<Question> updateUserRecommendedList(Long uid) {
        List<QuestionSubmit> problemSubmitList = problemSubmitMapper.selectList(new LambdaQueryWrapper<QuestionSubmit>().eq(QuestionSubmit::getUserId, uid));

        List<Question> res = new ArrayList<>();

        if (problemSubmitList.size() == 0) {
            //是新用户，还没提交过题目，就用协同过滤方法，考虑其他用户的行为来为他们提供一些推荐。
            //这里是用存放在Redis中的数据
            String s = stringRedisTemplate.opsForValue().get("recommended-list");
            res = JSONUtil.toList(s, Question.class);

            return res;
        } else {
            //得到用户提交题目的所有标签
            List<String> userSubmitTags = problemSubmitList.stream()
                    .map(QuestionSubmit::getQuestionId)
                    .map(problemMapper::selectById)
                    .filter(Objects::nonNull)
                    .map(Question::getTags)
                    .flatMap(tags -> JSONUtil.toList(tags, String.class).stream())
                    .collect(Collectors.toList());

            //随机得到用户没有通过的题目列表，数量为200条
            List<Question> failedProblemList = problemMapper.getRandomFailedProblemByUser(2, uid, 50);

            //对题目难度进行过滤，并用相似度进行排序
            failedProblemList = failedProblemList.stream()
                    .filter(problem -> {
                        List<String> tagsList = JSONUtil.toList(problem.getTags(), String.class);
                        double similarity = RecommendUtil.calculateSimilarity(userSubmitTags, tagsList);
                        problem.setSimilarity(similarity);
                        return isRecommendedByDifficulty(problemAverageDifficulty(uid), problem.getDifficulty());
                    })
                    .sorted(Comparator.comparingDouble(Question::getSimilarity))
                    .limit(RECOMMEND_NUM * 2)
                    .collect(Collectors.toList());

            //如果未通过且符合要求的题目超过50条，就在利用50的界限，不然就是是以failedProblemList为界限
            int recommendCount = Math.min(failedProblemList.size(), RECOMMEND_NUM);
            res.addAll(Stream.concat(
                            failedProblemList.subList(0, (recommendCount / 5) * 2).stream(),
                            failedProblemList.subList((recommendCount / 5) * 4, recommendCount).stream()
                    ).collect(Collectors.toList())
            );

            //在所有表单中随机搜索题目
            String s = stringRedisTemplate.opsForValue().get(RECOMMENDED_LIST);
            List<Question> problemList = JSONUtil.toList(s, Question.class);

            //todo 优化添加题目
//            if (problemList.size() > RECOMMEND_NUM * 2) {
//                int count = 0;
//                for (int i = 0; i < RECOMMEND_NUM - res.size(); i++) {
//                    if (count == problemList.size() * 2) {
//                        break;
//                    }
//
//                    int index = (int) (Math.random() * problemList.size());
//                    //如果已经存在了
//                    if (res.contains(problemList.get(index))) {
//                        i--;
//                        count++;
//                    } else {
//                        res.add(problemList.get(index));
//                        count++;
//                    }
//                }
//            }
            if (res.size()<RECOMMEND_NUM){
                res.addAll(problemList);
            }
            res = res.stream().distinct().limit(50).collect(Collectors.toList());
            return res;
        }


    }

    /**
     * 对题目中的难度进行过滤
     *
     * @param difficulty
     * @param s
     * @return
     */
    public boolean isRecommendedByDifficulty(double difficulty, String s) {
        boolean flag = false;

        if (s.equals("困难")) {
            flag = Math.abs(difficulty - 3) < 1;
        } else if (s.equals("中等")) {
            flag = Math.abs(difficulty - 2) < 1;
        } else {
            flag = Math.abs(difficulty - 1) < 1;
        }

        //有小部分题目不需要考虑题目难度和相似度，增加多样性
        if (Math.random() * 10 < 1) {
            flag = true;
        }

        return flag;
    }

    /**
     * 得到用户通过率以及难度给出平均难度系数
     *
     * @param userId
     * @return
     */
    public double problemAverageDifficulty(Long userId) {
        List<String> list = problemMapper.getDifficultyPassList(2, userId);

        if (list.size() == 0) {
            return 0;
        }

        double averageScore = list.stream()
                .mapToDouble(s -> {
                    if (s.equals("困难")) {
                        return 3.0;
                    } else if (s.equals("中等")) {
                        return 2.0;
                    } else {
                        return 1.0;
                    }
                })
                .average()
                .orElse(0.0);

        return averageScore;
    }

}
