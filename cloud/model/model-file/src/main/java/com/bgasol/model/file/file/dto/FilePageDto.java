package com.bgasol.model.file.file.dto;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bgasol.common.core.base.dto.BasePageDto;
import com.bgasol.model.file.file.entity.FileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@Setter
@SuperBuilder
@Schema(description = "文件分页查询参数")
public class FilePageDto extends BasePageDto<FileEntity> {
    @Schema(description = "文件名称")
    private String name;

    @Schema(description = "文件地址")
    private String url;

    @Schema(description = "文件大小范围-最大值")
    private Long maxLen;

    @Schema(description = "文件大小范围-最小值")
    private Long minLen;

    @Schema(description = "文件HASH")
    private String hash;

    @Schema(description = "文件状态")
    private String status;

    @Schema(description = "文件后缀")
    private String suffix;

    @Schema(description = "文件来源")
    private String source;

    @Schema(description = "文件所在桶")
    private String bucket;

    @Override
    public AbstractLambdaWrapper<FileEntity, LambdaQueryWrapper<FileEntity>> getQueryWrapper() {
        LambdaQueryWrapper<FileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ObjectUtils.isNotEmpty(name), FileEntity::getName, name);
        queryWrapper.like(ObjectUtils.isNotEmpty(url), FileEntity::getUrl, url);
        queryWrapper.like(ObjectUtils.isNotEmpty(hash), FileEntity::getHash, hash);
        queryWrapper.like(ObjectUtils.isNotEmpty(status), FileEntity::getStatus, status);
        queryWrapper.like(ObjectUtils.isNotEmpty(suffix), FileEntity::getSuffix, suffix);
        queryWrapper.like(ObjectUtils.isNotEmpty(source), FileEntity::getSource, source);
        queryWrapper.like(ObjectUtils.isNotEmpty(bucket), FileEntity::getBucket, bucket);
        queryWrapper.between(ObjectUtils.isNotEmpty(maxLen) && ObjectUtils.isNotEmpty(minLen), FileEntity::getSize, minLen, maxLen);
        return queryWrapper;
    }
}
