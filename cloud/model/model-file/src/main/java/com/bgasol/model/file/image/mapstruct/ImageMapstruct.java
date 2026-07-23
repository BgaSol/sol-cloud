package com.bgasol.model.file.image.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.file.image.dto.ImageCreateDto;
import com.bgasol.model.file.image.dto.ImageUpdateDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapstructConfig.class)
public interface ImageMapstruct {

    ImageMapstruct INSTANCE = Mappers.getMapper(ImageMapstruct.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "width", ignore = true)
    @Mapping(target = "height", ignore = true)
    @Mapping(target = "file", ignore = true)
    ImageEntity toEntity(ImageCreateDto dto);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "width", ignore = true)
    @Mapping(target = "height", ignore = true)
    @Mapping(target = "file", ignore = true)
    ImageEntity toEntity(ImageUpdateDto dto);
}
