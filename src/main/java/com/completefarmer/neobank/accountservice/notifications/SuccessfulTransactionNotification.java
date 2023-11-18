package com.completefarmer.neobank.accountservice.notifications;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;

public interface SuccessfulTransactionNotification extends Notification {
    void setAccount(AccountEntity account);
    void setTransaction(TransactionEntity transaction);
}
