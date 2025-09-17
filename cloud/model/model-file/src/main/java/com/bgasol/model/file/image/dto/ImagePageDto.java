package com.bgasol.model.file.image.dto;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.model.file.image.entity.ImageEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "图片分页查询参数")
public class ImagePageDto extends BasePageDto<ImageEntity> {
    @Schema(description = "图片名称")
    String name;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public Wrapper<ImageEntity> getQueryWrapper() {
        LambdaQueryWrapper<ImageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ObjectUtils.isNotEmpty(name), ImageEntity::getName, name);
        return queryWrapper;
    }
}
