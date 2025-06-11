package com.bgasol.model.file.file.mapstruct;

import com.bgasol.model.file.file.dto.FileCreateDto;
import com.bgasol.model.file.file.dto.FileUpdateDto;
import com.bgasol.model.file.file.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface FileMapstruct {

    FileMapstruct FILE_MAPSTRUCT_IMPL = Mappers.getMapper(FileMapstruct.class);

    FileEntity toEntity(FileCreateDto dto);

    FileEntity toEntity(FileUpdateDto dto);
}
