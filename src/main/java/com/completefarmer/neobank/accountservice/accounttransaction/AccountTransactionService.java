package com.completefarmer.neobank.accountservice.accounttransaction;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.enums.ETransactionType;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import com.completefarmer.neobank.accountservice.utils.exceptions.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * This class encapsulates logic and DB operations related to an Account and its transactions
 */
@Service
public class AccountTransactionService {

    private final AccountTransactionRepository accountTransactionRepository;

    @Autowired
    public AccountTransactionService(AccountTransactionRepository accountTransactionRepository) {
        this.accountTransactionRepository = accountTransactionRepository;
    }

    /**
     * Find all transactions belonging to an Account
     * @param account AccountEntity
     * @return List<AccountEntity>
     */
    public List<TransactionEntity> getAccountTransactions(AccountEntity account) {
        return accountTransactionRepository.findAllByAccountId(account.getId());
    }

    /**
     * Find a transaction with account and the transaction UUID
     * @param account that performed the transaction
     * @param transactionId UUID of the transaction
     * @return TransactionEntity if there's match
     */
    public TransactionEntity getAccountTransaction(AccountEntity account, String transactionId) {
        return accountTransactionRepository.findAllByAccountIdAndExternalId(
                account.getId(), UUID.fromString(transactionId)
        );
    }

    /**
     * Find account's recently updated transaction
     * @param account that performed the transaction
     * @return TransactionEntity if there's match
     */
    public TransactionEntity getRecentlyUpdatedTransaction(AccountEntity account, ETransactionStatus status) {
        return accountTransactionRepository.findTopByAccountIdAndStatusEquals(account.getId(), status);
    }

    /**
     * Checks if account has enough balance to perform new disbursement transaction
     * @param account the account performing the transaction
     * @param transaction transaction details
     * @throws InsufficientBalanceException if the account does not have enough funds
     */
    public void checkAccountBalance(AccountEntity account, TransactionEntity transaction) {
        if (transaction.getType() == ETransactionType.DISBURSEMENT && account.hasSufficientBalance(transaction.getAmount()))
            throw new InsufficientBalanceException("Insufficient balance");
    }

    /**
     * Fetch Transactions by account id and batch id
     * @param accountId of the account that performed the transaction
     * @param batchId of batch the transaction belongs to
     * @return List of TransactionEntities
     */
    public List<TransactionEntity> getTransactionsByAccountIdAndBatchId(Long accountId, Long batchId) {
        return accountTransactionRepository.findAllByAccountIdAndBatchId(accountId, batchId);
    }
}
