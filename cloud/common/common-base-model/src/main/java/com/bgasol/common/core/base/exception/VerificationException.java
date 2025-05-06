package com.bgasol.common.core.base.exception;

import com.bgasol.common.core.base.vo.VerificationResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
public class VerificationException extends RuntimeException {

    private final List<VerificationResult> verificationResults;

    public VerificationException(String message, List<VerificationResult> verificationResults) {
        super(message);
        this.verificationResults = verificationResults;
    }
}
