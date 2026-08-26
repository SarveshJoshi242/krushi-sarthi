package com.krushiadhaar.common.exception;
public class UnauthorizedResourceAccessException extends DomainException {
    public UnauthorizedResourceAccessException(String message) {
        super(message, "UNAUTHORIZED_ACCESS");
    }
}
