package com.bgasol.common.core.base.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.bgasol.common.core.base.dto.BaseCreateDto;
import com.bgasol.common.core.base.entity.BaseEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 通用 Excel 导入监听器：
 * - 支持两种读法：
 * 1) 直接读取实体（Entity 直读）
 * 2) 读取 DTO 后映射为实体（DTO -> Entity）
 * - 通过工厂方法创建，业务仅提供批量保存、校验、异常处理回调
 */
public class BaseExcelImportListener<ROW, ENTITY extends BaseEntity> implements ReadListener<ROW> {
    private final int batchSize;
    private final List<ENTITY> buffer = new ArrayList<>();
    @Getter
    private final List<String> errors = new ArrayList<>();
    @Getter
    private final List<ENTITY> savedEntities = new ArrayList<>();
    @Getter
    private int currentRowIndex = 0; // 从数据行开始计数（不含表头）
    @Getter
    private int successRows = 0; // 成功持久化的行数

    private final BatchSaver<ENTITY> saver;
    private final RowValidator<ENTITY> validator;
    private final ErrorHandler errorHandler;
    private final Function<ROW, ENTITY> rowMapper;

    private BaseExcelImportListener(
            int batchSize,
            BatchSaver<ENTITY> saver,
            RowValidator<ENTITY> validator,
            ErrorHandler errorHandler,
            Function<ROW, ENTITY> rowMapper
    ) {
        this.batchSize = Math.max(1, batchSize);
        this.saver = saver;
        this.validator = validator;
        this.errorHandler = errorHandler;
        this.rowMapper = rowMapper;
    }

    @FunctionalInterface
    public interface BatchSaver<ENTITY> {
        void saveBatch(List<ENTITY> batch) throws Exception;
    }

    @FunctionalInterface
    public interface RowValidator<ENTITY> {
        boolean validate(ENTITY entity, int rowIndex,List<String> errors) throws Exception;
    }

    @FunctionalInterface
    public interface ErrorHandler {
        void onError(Exception e, int rowIndex);
    }

    /**
     * 工厂：实体直读
     */
    public static <ENTITY extends BaseEntity> BaseExcelImportListener<ENTITY, ENTITY> ofEntity(
            int batchSize,
            BatchSaver<ENTITY> saver,
            RowValidator<ENTITY> validator,
            ErrorHandler errorHandler) {
        return new BaseExcelImportListener<>(batchSize, saver, validator, errorHandler, Function.identity());
    }

    /**
     * 工厂：DTO -> 实体
     */
    public static <DTO extends BaseCreateDto<ENTITY>, ENTITY extends BaseEntity> BaseExcelImportListener<DTO, ENTITY> ofDto(
            int batchSize,
            BatchSaver<ENTITY> saver,
            RowValidator<ENTITY> validator,
            ErrorHandler errorHandler) {
        return new BaseExcelImportListener<>(batchSize, saver, validator, errorHandler, BaseCreateDto::toEntity);
    }

    /**
     * 行回调：行映射 -> 校验 -> 缓冲 -> 达批次后 flush
     */
    @Override
    public void invoke(ROW row, AnalysisContext context) {
        currentRowIndex++;
        try {
            if (row == null) {
                // 空行直接跳过
                return;
            }
            ENTITY entity = rowMapper.apply(row);
            // 若配置了校验器且未通过，则跳过当前行
            if (validator != null && !validator.validate(entity, currentRowIndex,errors)) {
                return;
            }
            buffer.add(entity);
            if (buffer.size() >= batchSize) {
                flush();
            }
        } catch (Exception e) {
            onError(e, currentRowIndex);
        }
    }

    /**
     * 读取完成：最终 flush
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        flush();
    }

    /**
     * EasyExcel 异常回调：委托到 onError
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        throw new RuntimeException(exception);
//        onError(exception, currentRowIndex);
    }

    /**
     * 刷新：批量保存缓冲区数据
     */
    private void flush() {
        if (buffer.isEmpty()) {
            return;
        }
        try {
            saver.saveBatch(buffer);
            // 认为整个批次均成功
            savedEntities.addAll(buffer);
            successRows += buffer.size();
        } catch (Exception e) {
            onError(e, currentRowIndex);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 错误处理：若提供 errorHandler 则回调，否则默认收集错误信息
     */
    private void onError(Exception e, int rowIndex) {
        if (errorHandler != null) {
            errorHandler.onError(e, rowIndex);
        } else {
            String type = e.getClass().getSimpleName();
            errors.add("第" + rowIndex + "行错误[" + type + "]: " + e.getMessage());
        }
    }
}
