package com.yu.judge.codesandbox;


import com.yu.model.codesandbox.ExecuteCodeRequest;
import com.yu.model.codesandbox.ExecuteCodeResponse;

/**
 * @author Shier
 * @createTime 2023/8/30 星期三 10:22
 * 代码沙箱接口定义
 */
public interface CodeSandBox {

    /**
     * 代码沙箱执行代码接口
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
