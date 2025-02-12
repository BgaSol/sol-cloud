package com.bgasol.web.system.user.mapper;

import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.model.system.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends MyBaseMapper<UserEntity> {
}
