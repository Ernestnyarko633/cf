package com.completefarmer.neobank.accountservice.batch;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.account.AccountService;
import com.completefarmer.neobank.accountservice.accounttransaction.AccountTransactionService;
import com.completefarmer.neobank.accountservice.accounttransaction.CreateTransactionRequest;
import com.completefarmer.neobank.accountservice.accounttransaction.CustomAccountTransactionRepository;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import com.completefarmer.neobank.accountservice.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class that interacts with the AccountBatchRepository
 */
@Service
public class AccountBatchService {
    final AccountBatchRepository accountBatchRepository;

    final TransactionService transactionService;

    final AccountTransactionService accountTransactionService;

    final CustomAccountTransactionRepository customAccountTransactionRepository;

    final AccountService accountService;

    @Autowired
    public AccountBatchService(
            AccountBatchRepository accountBatchRepository,
            TransactionService transactionService,
            AccountTransactionService accountTransactionService,
            CustomAccountTransactionRepository customAccountTransactionRepository,
            AccountService accountService
    ) {
        this.accountBatchRepository = accountBatchRepository;
        this.transactionService = transactionService;
        this.accountTransactionService = accountTransactionService;
        this.customAccountTransactionRepository = customAccountTransactionRepository;
        this.accountService = accountService;
    }

    /**
     * Create a new BatchEntity and maps transactions to it
     *
     * @param account                      the account creating the batch
     * @param createBatchRequest           request details for the batch
     * @param createTransactionRequestList list of Transaction data to create transactions associated with the batch
     * @return a single BatchEntity
     */
    public Optional<BatchEntity> save(AccountEntity account, CreateBatchRequest createBatchRequest, List<CreateTransactionRequest> createTransactionRequestList) {
        BatchEntity batch = new BatchEntity(
                createBatchRequest.getName(),
                createBatchRequest.getDescription(),
                createBatchRequest.getProcessAt(),
                account,
                ETransactionStatus.QUEUED
        );

        accountBatchRepository.save(batch);
        ArrayList<String> accountNumbers = new ArrayList<>();
        Long totalTransactionValue = 0L;

        for (CreateTransactionRequest transactionRequest : createTransactionRequestList) {
            TransactionEntity transaction = new TransactionEntity(
                    transactionRequest.getAmount(),
                    transactionRequest.getNarration(),
                    transactionRequest.getType(),
                    transactionRequest.getCallbackUrl(),
                    transactionRequest.getAccountNumber(),
                    transactionRequest.getAccountIssuer(),
                    transactionRequest.getAccountName(),
                    transactionRequest.getClientReference(),
                    transactionRequest.getBalanceBefore(),
                    transactionRequest.getBalanceAfter(),
                    transactionRequest.getProcessAt(),
                    transactionRequest.getBatch()
            );
            transaction.setStatus(ETransactionStatus.QUEUED);
            accountNumbers.add(transactionRequest.getAccountNumber());
            accountTransactionService.checkAccountBalance(account, transaction);
            setTransactionBalances(account, createBatchRequest, transactionRequest, batch);
            transactionService.save(account, transaction);
            totalTransactionValue += transactionRequest.getAmount();
        }

        Set<String> uniqueAccountNumbers = new HashSet<>(accountNumbers.stream().toList());
        setBatchStats(batch, accountNumbers, uniqueAccountNumbers, totalTransactionValue);
        accountBatchRepository.save(batch);
        updateAccountBalance(account);

        return accountBatchRepository.findByExternalId(batch.getExternalId());
    }

    /**
     * Updates balance before and after the transaction if performed
     * Sets processAt date if present
     *
     * @param account            creating the batch
     * @param createBatchRequest contains the batch details including a list of transactions
     * @param transactionRequest contains transaction details
     * @param batch              the batch the transactions belong to
     */
    private static void setTransactionBalances(
            AccountEntity account,
            CreateBatchRequest createBatchRequest,
            CreateTransactionRequest transactionRequest,
            BatchEntity batch
    ) {
        transactionRequest.setBalanceBefore(account.getAvailableBalance());
        transactionRequest.setBalanceAfter(account.getAvailableBalance() - transactionRequest.getAmount());
        transactionRequest.setBatch(batch);
        transactionRequest.setProcessAt(
                transactionRequest.getProcessAt() != null ?
                        transactionRequest.getProcessAt() :
                        createBatchRequest.getProcessAt()
        );
    }

    /**
     * Updates Accounts balance before and after the batch
     *
     * @param account performing the transaction
     */
    private void updateAccountBalance(AccountEntity account) {
        Long availableBalance = customAccountTransactionRepository.getAvailableBalance(account);
        account.setAvailableBalance(availableBalance);
        account.setActualBalance(customAccountTransactionRepository.getActualBalance(account));
        accountService.update(account, availableBalance);
    }

    /**
     * Sets Batch's statistics
     *
     * @param batch                 to update
     * @param accountNumbers        involved in the transactions
     * @param uniqueAccountNumbers  unique account numbers
     * @param totalTransactionValue of all transactions in the batch
     */
    private static void setBatchStats(
            BatchEntity batch,
            ArrayList<String> accountNumbers,
            Set<String> uniqueAccountNumbers,
            Long totalTransactionValue
    ) {
        batch.setTotalTransactionCount(accountNumbers.size());
        batch.setTotalPendingCount(accountNumbers.size());
        batch.setTotalUniqueCount(uniqueAccountNumbers.size());
        batch.setTotalTransactionValue(totalTransactionValue);
        batch.setTotalDuplicateCount(accountNumbers.size() - uniqueAccountNumbers.size());
    }

    /**
     * Get Batch by account id and batch UUID
     *
     * @param account account that created the batch
     * @return Lists of BatchEntities
     */
    public List<BatchEntity> getAccountBatches(AccountEntity account) {
        return accountBatchRepository.findAllByAccountId(account.getId());
    }

    /**
     * Get Batch by account id and batch UUID
     *
     * @param account         account that created the batch
     * @param batchExternalId batch UUID
     * @return BatchEntity if there's a match
     */
    public Optional<BatchEntity> getAccountBatch(AccountEntity account, UUID batchExternalId) {
        return accountBatchRepository.findByAccountIdAndExternalId(account.getId(), batchExternalId);
    }
}
