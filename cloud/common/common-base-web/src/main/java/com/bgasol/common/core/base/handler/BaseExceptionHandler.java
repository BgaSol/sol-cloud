package com.bgasol.common.core.base.handler;

import com.bgasol.common.core.base.exception.BaseException;
import com.bgasol.common.core.base.vo.BaseVo;
import com.bgasol.common.core.base.vo.VerificationResult;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(value = BaseException.class)
    @ApiResponse(description = "业务异常", responseCode = "500")
    public BaseVo<?> baseExceptionHandler(BaseException e) {
        log.error("业务异常", e);
        return e.getBaseVo();
    }

    /**
     * 处理参数校验异常 方法参数级 MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(description = "参数校验异常", responseCode = "400")
    public BaseVo<List<VerificationResult>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
        return BaseVo.code400(verificationResults);
    }

    /**
     * 处理参数校验异常 类字段级 ConstraintViolationException
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ApiResponse(description = "参数校验异常", responseCode = "400")
    public BaseVo<List<VerificationResult>> constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
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
        return BaseVo.code400(verificationResults);
    }
}
