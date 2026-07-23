package com.bgasol.model.file.file.mapstruct;

import com.bgasol.common.core.base.mapstruct.BaseMapstructConfig;
import com.bgasol.model.file.file.dto.FileCreateDto;
import com.bgasol.model.file.file.dto.FileUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = BaseMapstructConfig.class)
public interface FileMapstruct {

    FileMapstruct INSTANCE = Mappers.getMapper(FileMapstruct.class);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "bucket", ignore = true)
    FileEntity toEntity(FileCreateDto dto);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "suffix", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "bucket", ignore = true)
    FileEntity toEntity(FileUpdateDto dto);
}
