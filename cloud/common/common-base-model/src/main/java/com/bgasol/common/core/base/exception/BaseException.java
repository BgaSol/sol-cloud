package com.bgasol.common.core.base.exception;

import com.bgasol.common.core.base.vo.ResponseType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class BaseException extends RuntimeException {
    private final ResponseType responseType;
    private final Boolean isPrimary;
    private final Integer code;

    @Builder
    public BaseException(String message, Throwable cause, ResponseType responseType, Boolean isPrimary, Integer code) {
        super(message, cause);
        this.responseType = responseType != null ? responseType : ResponseType.ERROR;
        this.isPrimary = isPrimary != null ? isPrimary : false;
        this.code = code != null ? code : -1;
    }

    public BaseException(String message) {
        this(message, null, null, null, null);
    }

    public BaseException(String message, Throwable cause) {
        this(message, cause, null, null, null);
    }
}
