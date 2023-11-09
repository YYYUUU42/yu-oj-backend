package com.yu.problem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.model.entity.Question;
import com.yu.model.vo.ProblemCompleteCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 27583
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2023-11-02 21:00:37
* @Entity generator.domain.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    List<Question> getRandomFailedProblemByUser(@Param("status")Integer status, @Param("userId") Long userId, @Param("num")Integer num);

    List<String> getDifficultyPassList(@Param("status")Integer status,@Param("userId") Long userId);
    List<ProblemCompleteCountVo> getProblemCompleteCount(@Param("status")Integer status);
}




