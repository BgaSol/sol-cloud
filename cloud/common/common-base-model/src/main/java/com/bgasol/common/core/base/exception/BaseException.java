package com.bgasol.common.core.base.exception;

import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.ResponseType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/// 自定义通用异常
@Getter
@Slf4j
public class BaseException extends RuntimeException {

    private final BaseVo<?> baseVo;

    public BaseException(BaseVo<?> baseVo) {
        super(baseVo.getMessage());
        this.baseVo = baseVo;
    }

    public BaseException(String message) {
        super(message);
        this.baseVo = BaseVo.error(message);
    }

    public BaseException(String message, ResponseType type) {
        super(message);
        this.baseVo = BaseVo.error(message, type);
    }
}
