package com.bgasol.common.core.base.handler;

import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.exception.VerificationException;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.VerificationResult;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static com.bgasol.common.constant.value.SystemConfigValues.REQUEST_EXCEPTION;
import static com.bgasol.common.constant.value.SystemConfigValues.REQUEST_EXCEPTION_PRIMARY;

/**
 * 全局异常处理
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class BaseExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(value = BaseException.class)
    @ApiResponse(description = "业务异常", responseCode = "500")
    public BaseVo<Void> baseExceptionHandler(BaseException e, HttpServletRequest request) {
        if (e.getIsPrimary()) {
            request.setAttribute(REQUEST_EXCEPTION_PRIMARY, true);
        }
        request.setAttribute(REQUEST_EXCEPTION, ExceptionUtils.getStackTrace(e));
        return BaseVo.error(e.getMessage(), e.getResponseType());
    }

    @ExceptionHandler(value = VerificationException.class)
    @ApiResponse(description = "参数校验异常", responseCode = "400")
    public BaseVo<List<VerificationResult>> verificationExceptionHandler(VerificationException e, HttpServletRequest request) {
        List<VerificationResult> verificationResults = e.getVerificationResults();
        request.setAttribute(REQUEST_EXCEPTION, "参数校验异常");
        return BaseVo.code400(verificationResults);
    }

    /**
     * 处理参数校验异常 方法参数级 MethodArgumentNotValidException
     */
    @SneakyThrows
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(description = "参数校验异常", responseCode = "400")
    public BaseVo<List<VerificationResult>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<VerificationResult> verificationResults = new ArrayList<>();
        for (ObjectError allError : allErrors) {
            VerificationResult verificationResult = new VerificationResult();
            // 提取校验消息
            verificationResult.setMessage(allError.getDefaultMessage());
            // 提取字段名
            verificationResult.setField((String) allError.getClass().getMethod("getField").invoke(allError));
            verificationResult.setResult(false);
            verificationResults.add(verificationResult);
        }
        request.setAttribute(REQUEST_EXCEPTION, "参数校验异常");
        return BaseVo.code400(verificationResults);
    }

    /**
     * 处理参数校验异常 类字段级 ConstraintViolationException
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ApiResponse(description = "参数校验异常", responseCode = "400")
    public BaseVo<List<VerificationResult>> constraintViolationExceptionHandler(ConstraintViolationException e, HttpServletRequest request) {
        List<VerificationResult> verificationResults = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            VerificationResult verificationResult = new VerificationResult();
            String field = null;
            // 提取字段名
            for (Path.Node node : constraintViolation.getPropertyPath()) {
                field = node.getName();
            }
            verificationResult.setField(field);
            // 提取校验消息
            verificationResult.setMessage(constraintViolation.getMessage());
            verificationResult.setResult(false);
            verificationResults.add(verificationResult);
        }
        request.setAttribute(REQUEST_EXCEPTION, "参数校验异常");
        return BaseVo.code400(verificationResults);
    }
}
