package com.krushiadhaar.common.exception;

import com.krushiadhaar.common.exception.DomainException;
import lombok.Getter;
@Getter
public class ApiException extends DomainException {
    private final String code;
    public ApiException(String code, String message) {
        super(message);
        this.code = code;
    }
}
