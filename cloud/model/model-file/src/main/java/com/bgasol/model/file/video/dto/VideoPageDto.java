package com.bgasol.model.file.video.dto;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.model.file.video.entity.VideoEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "图片分页查询参数")
public class VideoPageDto extends BasePageDto<VideoEntity> {
    @Schema(description = "视频名称")
    private String name;

    @Schema(description = "大于（秒）视频时长")
    private Integer duration;

    @Schema(description = "视频格式")
    private String format;

    @Schema(description = "视频编码")
    private String codec;

    @Override
    @JsonIgnore
    @Schema(hidden = true)
    public AbstractLambdaWrapper<VideoEntity, LambdaQueryWrapper<VideoEntity>> getQueryWrapper() {
        LambdaQueryWrapper<VideoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ObjectUtils.isNotEmpty(name), VideoEntity::getName, name);
        queryWrapper.gt(ObjectUtils.isNotEmpty(duration), VideoEntity::getDuration, duration);
        queryWrapper.eq(ObjectUtils.isNotEmpty(format), VideoEntity::getFormat, format);
        queryWrapper.eq(ObjectUtils.isNotEmpty(codec), VideoEntity::getCodec, codec);
        return queryWrapper;
    }
}
