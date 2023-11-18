package com.completefarmer.neobank.accountservice.account;

import com.completefarmer.neobank.accountservice.accounttransaction.CustomAccountTransactionRepository;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

/**
 * This class listens to TransactionEntity lifecycle events
 * and logs
 */
@Slf4j
public class TransactionEntityListener {

    @PrePersist
    private void beforePersist(TransactionEntity transaction) {
        log.info("Attempting to create new transaction: " + transaction.toString());
    }

    @PostPersist
    private void afterPersist(TransactionEntity transaction) {
        log.info("New transaction created: " + transaction.toString());
    }

    @PreUpdate
    private void beforeUpdate(TransactionEntity transaction) {
        log.info("Attempting to update transaction: " + transaction.toString());
    }

    @PostUpdate
    private void afterUpdate(TransactionEntity transaction) {
        log.info("Transaction updated: " + transaction.toString());
    }
}
