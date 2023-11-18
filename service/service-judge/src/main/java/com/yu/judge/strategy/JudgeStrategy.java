package com.yu.judge.strategy;


import com.yu.model.codesandbox.JudgeInfo;

/**
 * 判题策略
 * 
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}