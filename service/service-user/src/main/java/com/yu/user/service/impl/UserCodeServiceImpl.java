package com.yu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.common.common.ErrorCode;
import com.yu.common.exception.BusinessException;
import com.yu.common.exception.ThrowUtils;
import com.yu.model.entity.UserCode;
import com.yu.user.mapper.UserCodeMapper;
import com.yu.user.service.UserCodeService;
import org.springframework.stereotype.Service;

/**
 * @author 27583
 * @description 针对表【user_code(用户编号表)】的数据库操作Service实现
 * @createDate 2023-11-02 21:00:37
 */
@Service
public class UserCodeServiceImpl extends ServiceImpl<UserCodeMapper, UserCode> implements UserCodeService {
    @Override
    public UserCode getUserCodeByUserId(long userId) {
        if (userId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserCode> wrapper = new QueryWrapper<>();
        wrapper.eq("userId", userId);
        UserCode userCode = this.getOne(wrapper);
        ThrowUtils.throwIf(userCode == null, ErrorCode.NULL_ERROR, "此用户不存在");
        return userCode;
    }
}




