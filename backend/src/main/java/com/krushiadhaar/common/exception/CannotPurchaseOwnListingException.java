package com.krushiadhaar.common.exception;
public class CannotPurchaseOwnListingException extends DomainException {
    public CannotPurchaseOwnListingException(String message) {
        super(message, "CANNOT_PURCHASE_OWN_LISTING");
    }
}
