package com.completefarmer.neobank.accountservice.utils.exceptions;

public class BadTransactionStatusException extends RuntimeException {
    public BadTransactionStatusException(String message) {
        super(message);
    }
}
