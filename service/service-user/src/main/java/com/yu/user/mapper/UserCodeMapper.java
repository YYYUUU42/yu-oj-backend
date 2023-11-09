package com.yu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.model.entity.UserCode;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 27583
* @description 针对表【user_code(用户编号表)】的数据库操作Mapper
* @createDate 2023-11-02 21:00:37
* @Entity generator.domain.UserCode
*/
@Mapper
public interface UserCodeMapper extends BaseMapper<UserCode> {

}




