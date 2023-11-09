package com.yu.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yu.common.common.ErrorCode;
import com.yu.common.exception.BusinessException;
import com.yu.judge.codesandbox.CodeSandBox;
import com.yu.model.codesandbox.ExecuteCodeRequest;
import com.yu.model.codesandbox.ExecuteCodeResponse;


/**
 * 远程代码沙箱（实际调用接口的沙箱）
 *
 * @author Shier
 */
public class RemoteCodeSandbox implements CodeSandBox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    private static final String RemoteCodeSandboxURL="http://118.178.230.79:8001/executeCode";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        // todo 修改成线上的接口
        String url = RemoteCodeSandboxURL;
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
               .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
               .body(json)
               .execute()
               .body();
        if (StringUtils.isBlank(responseStr)) {
           throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
