package com.bgasol.model.file.image.mapstruct;

import com.bgasol.model.file.image.dto.ImageCreateDto;
import com.bgasol.model.file.image.dto.ImageUpdateDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface ImageMapstruct {

    ImageMapstruct IMAGE_MAPSTRUCT = Mappers.getMapper(ImageMapstruct.class);

    ImageEntity toEntity(ImageCreateDto dto);

    ImageEntity toEntity(ImageUpdateDto dto);
}
