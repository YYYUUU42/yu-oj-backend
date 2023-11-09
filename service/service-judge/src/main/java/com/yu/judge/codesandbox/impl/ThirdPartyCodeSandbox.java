package com.yu.judge.codesandbox.impl;


import com.yu.judge.codesandbox.CodeSandBox;
import com.yu.model.codesandbox.ExecuteCodeRequest;
import com.yu.model.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
