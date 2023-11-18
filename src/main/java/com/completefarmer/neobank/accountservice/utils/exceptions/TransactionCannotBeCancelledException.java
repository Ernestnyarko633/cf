package com.completefarmer.neobank.accountservice.utils.exceptions;

/**
 * Failed Scheduled Transaction Cancellation Exception
 */
public class TransactionCannotBeCancelledException extends RuntimeException {
    public TransactionCannotBeCancelledException(String message) {
        super(message);
    }
}
