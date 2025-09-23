package com.bgasol.common.core.base.converter;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将单元格内容转换为 List<String>，默认按逗号分隔。
 * - 空/空白单元格：返回空列表
 */
public class StringToStringListConverter implements Converter<List<String>> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public List<String> convertToJavaData(ReadCellData<?> cellData,
                                          ExcelContentProperty contentProperty,
                                          GlobalConfiguration globalConfiguration) {
        if (cellData == null) {
            return Collections.emptyList();
        }

        String raw = extractAsString(cellData);
        if (raw == null) {
            return Collections.emptyList();
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return Collections.emptyList();
        }

        // 使用常见分隔符：英文逗号
        String[] parts = trimmed.split(",");
        return Arrays.stream(parts)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String extractAsString(ReadCellData<?> cellData) {
        CellDataTypeEnum type = cellData.getType();
        if (type == null) {
            return null;
        }
        return switch (type) {
            case NUMBER -> cellData.getNumberValue() == null ? null : cellData.getNumberValue().toPlainString();
            case BOOLEAN -> cellData.getBooleanValue() == null ? null : String.valueOf(cellData.getBooleanValue());
            case EMPTY -> StringUtils.EMPTY;
            default -> cellData.getStringValue();
        };
    }
}
