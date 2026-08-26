package com.krushiadhaar.common.exception;
public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}
