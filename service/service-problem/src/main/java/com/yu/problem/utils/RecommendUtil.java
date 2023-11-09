package com.yu.problem.utils;

import org.apache.commons.text.similarity.CosineSimilarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算相似度工具类
 */
public class RecommendUtil {

    /**
     * 计算余弦相似度
     *
     * @param userTags
     * @param tags
     * @return
     */
    public static double calculateSimilarity(List<String> userTags, List<String> tags) {
        Map<String, Integer> userTagFrequency = new HashMap<>();
        Map<String, Integer> problemTagFrequency = new HashMap();

        // 统计用户标签和问题标签的词频
        for (String tag : userTags) {
            userTagFrequency.put(tag, userTagFrequency.getOrDefault(tag, 0) + 1);
        }
        for (String tag : tags) {
            problemTagFrequency.put(tag, problemTagFrequency.getOrDefault(tag, 0) + 1);
        }

        // 创建用户标签向量和问题标签向量，使用标签词频作为权重
        double[] userVector = createWeightedVector(userTagFrequency);
        double[] problemVector = createWeightedVector(problemTagFrequency);


        // 通过零填充确保向量长度相等
        int maxLength = Math.max(userVector.length, problemVector.length);
        userVector = padVector(userVector, maxLength);
        problemVector = padVector(problemVector, maxLength);

        // 将数组转换为 Map<CharSequence, Integer>
        Map<CharSequence, Integer> userVectorMap = new HashMap<>();
        Map<CharSequence, Integer> problemVectorMap = new HashMap<>();

        for (int i = 0; i < userVector.length; i++) {
            userVectorMap.put(String.valueOf(i), (int) Math.round(userVector[i]));
            problemVectorMap.put(String.valueOf(i), (int) Math.round(problemVector[i]));
        }

        // 使用余弦相似度计算
        CosineSimilarity cosine = new CosineSimilarity();
        double similarity = cosine.cosineSimilarity(userVectorMap, problemVectorMap);

        return similarity;
    }

    /**
     * 零填充向量至指定长度
     *
     * @param vector
     * @param length
     * @return
     */
    private static double[] padVector(double[] vector, int length) {
        double[] paddedVector = new double[length];
        System.arraycopy(vector, 0, paddedVector, 0, vector.length);
        return paddedVector;
    }

    /**
     * 创建加权标签向量
     *
     * @param tagFrequency
     * @return
     */
    public static double[] createWeightedVector(Map<String, Integer> tagFrequency) {
        double[] vector = new double[tagFrequency.size()];
        int index = 0;
        for (String tag : tagFrequency.keySet()) {
            vector[index] = tagFrequency.get(tag);
            index++;
        }
        return vector;
    }
}
