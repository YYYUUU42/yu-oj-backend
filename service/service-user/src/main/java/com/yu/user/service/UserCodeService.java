package com.yu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.model.entity.UserCode;

/**
* @author 27583
* @description 针对表【user_code(用户编号表)】的数据库操作Service
* @createDate 2023-11-02 21:00:37
*/
public interface UserCodeService extends IService<UserCode> {

    /**
     * 查看用户有无调用次数
     * @param userId
     * @return
     */
    UserCode getUserCodeByUserId(long userId);
}
