package com.bgasol.model.file.video.mapstruct;

import com.bgasol.model.file.video.dto.VideoCreateDto;
import com.bgasol.model.file.video.dto.VideoUpdateDto;
import com.bgasol.model.file.video.entity.VideoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface VideoMapstruct {

    VideoMapstruct VIDEO_MAPSTRUCT = Mappers.getMapper(VideoMapstruct.class);

    VideoEntity toEntity(VideoCreateDto dto);

    VideoEntity toEntity(VideoUpdateDto dto);
}
