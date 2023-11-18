package com.completefarmer.neobank.accountservice.utils.exceptions;

public class BadTransactionCandidateException extends RuntimeException {
    public BadTransactionCandidateException(String message) {
        super(message);
    }
}
