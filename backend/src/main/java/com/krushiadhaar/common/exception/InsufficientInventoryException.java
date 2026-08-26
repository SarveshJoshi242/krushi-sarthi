package com.krushiadhaar.common.exception;
public class InsufficientInventoryException extends DomainException {
    public InsufficientInventoryException(String message) {
        super(message, "INSUFFICIENT_INVENTORY");
    }
}
