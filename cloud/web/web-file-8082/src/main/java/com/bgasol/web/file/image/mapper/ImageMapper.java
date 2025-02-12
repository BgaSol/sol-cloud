package com.bgasol.web.file.image.mapper;

import com.bgasol.common.core.base.mapper.MyBaseMapper;
import com.bgasol.model.file.image.entity.ImageEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageMapper extends MyBaseMapper<ImageEntity> {
}
