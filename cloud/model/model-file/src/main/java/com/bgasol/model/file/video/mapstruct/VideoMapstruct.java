package com.bgasol.model.file.video.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.file.video.dto.VideoCreateDto;
import com.bgasol.model.file.video.dto.VideoUpdateDto;
import com.bgasol.model.file.video.entity.VideoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapstructConfig.class)
public interface VideoMapstruct {

    VideoMapstruct INSTANCE = Mappers.getMapper(VideoMapstruct.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "file", ignore = true)
    VideoEntity toEntity(VideoCreateDto dto);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "file", ignore = true)
    VideoEntity toEntity(VideoUpdateDto dto);
}
