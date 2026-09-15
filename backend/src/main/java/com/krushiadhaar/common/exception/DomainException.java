package com.krushiadhaar.common.exception;

import lombok.Getter;
@Getter
public class DomainException extends RuntimeException {
    private final String errorCode;
    public DomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
    }
    public DomainException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
    }
    public DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
