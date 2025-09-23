package com.bgasol.model.file.file.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "文件状态：LOADING, SUCCESS")
public enum FileStaus {
    LOADING("LOADING"),
    SUCCESS("SUCCESS");

    @EnumValue()
    private final String value;
}