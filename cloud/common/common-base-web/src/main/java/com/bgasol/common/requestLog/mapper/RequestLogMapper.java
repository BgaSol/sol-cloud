package com.bgasol.common.requestLog.mapper;

import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.model.system.requestLog.entity.RequestLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RequestLogMapper extends MyBaseMapper<RequestLogEntity> {
}
