package com.bgasol.model.file.video.dto;

import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.model.file.video.entity.VideoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Schema(description = "创建视频实体类")
public class VideoCreateDto extends BaseCreateDto<VideoEntity> {
    @Schema(description = "视频名称")
    private String name;

    @Schema(description = "视频宽度")
    private Integer width;

    @Schema(description = "视频高度")
    private Integer height;

    @Schema(description = "视频时长（秒）")
    private Integer duration;

    @Schema(description = "视频格式")
    private String format;

    @Schema(description = "视频码率")
    private Integer bitrate;

    @Schema(description = "视频帧率")
    private Integer frameRate;

    @Schema(description = "视频编码")
    private String codec;

    @Schema(description = "视频文件id")
    private String fileId;

    @Override
    public VideoEntity toEntity() {
        return this.toEntity(VideoEntity.builder()
                .name(name)
                .width(width)
                .height(height)
                .duration(duration)
                .format(format)
                .bitrate(bitrate)
                .frameRate(frameRate)
                .codec(codec)
                .fileId(fileId)
                .build());
    }
}
