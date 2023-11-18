package com.completefarmer.neobank.accountservice.batch;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.account.AccountService;
import com.completefarmer.neobank.accountservice.accounttransaction.AccountTransactionService;
import com.completefarmer.neobank.accountservice.transaction.TransactionDTOMapper;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import com.completefarmer.neobank.accountservice.utils.responses.JsonResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller that handles all requests to /accounts/accountID/batches....
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountBatchController {

    private final AccountService accountService;
    private final AccountBatchService accountBatchService;
    private final BatchDTOMapper batchDTOMapper;
    private final AccountTransactionService accountTransactionService;
    private final TransactionDTOMapper transactionDTOMapper;

    @Autowired
    public AccountBatchController(AccountService accountService, AccountBatchService accountBatchService, BatchDTOMapper batchDTOMapper, AccountTransactionService accountTransactionService, TransactionDTOMapper transactionDTOMapper) {
        this.accountService = accountService;
        this.accountBatchService = accountBatchService;
        this.batchDTOMapper = batchDTOMapper;
        this.accountTransactionService = accountTransactionService;
        this.transactionDTOMapper = transactionDTOMapper;
    }


    /**
     * Creates a new batch by receiving http requests
     * it passes predefined validation logic against and incoming requests
     *
     * @param batchRequest request class with validation
     * @param issuerId     account id of the account performing the transaction
     * @return a BatchDTO
     */
    @Transactional
    @PostMapping("/{issuerId}/batches")
    public ResponseEntity<?> create(@RequestBody @Valid CreateBatchRequest batchRequest, @PathVariable String issuerId) {
        AccountEntity account = accountService.findByIssuerId(issuerId).get();
        BatchEntity batch = accountBatchService.save(account, batchRequest, batchRequest.getTransactions()).get();
        return JsonResponse.set(HttpStatus.CREATED, "Batch created successfully.", batchDTOMapper.apply(batch));
    }

    /**
     * Get All batches that belong to an account
     *
     * @param issuerId account id of the neobank account
     * @return List of BatchDTO
     */
    @GetMapping("/{issuerId}/batches")
    public ResponseEntity<?> getBatches(@PathVariable String issuerId) {
        AccountEntity account = accountService.findByIssuerId(issuerId).get();
        List<BatchEntity> batches = accountBatchService.getAccountBatches(account);

        return JsonResponse.set(HttpStatus.OK, "Operation successful.", batches.stream().map(batchDTOMapper).toList());
    }

    /**
     * Gets a single BatchDTO
     *
     * @param issuerId        account number
     * @param batchExternalId UUID of the batch
     * @return single BatchDTO
     */
    @GetMapping("/{issuerId}/batches/{batchExternalId}")
    public ResponseEntity<?> getBatch(@PathVariable String issuerId, @PathVariable UUID batchExternalId) {
        AccountEntity account = accountService.findByIssuerId(issuerId).get();
        BatchEntity batch = accountBatchService.getAccountBatch(account, batchExternalId).get();

        return JsonResponse.set(HttpStatus.OK, "Operation successful.", batchDTOMapper.apply(batch));
    }


    /**
     * Get All transactions associate with a Batch
     *
     * @param issuerId        account number that created the batch
     * @param batchExternalId UUID of the batch
     * @return list of TransactionDTO that belong to the batch
     */
    @GetMapping("/{issuerId}/batches/{batchExternalId}/transactions")
    public ResponseEntity<?> getBatchTransactions(@PathVariable String issuerId, @PathVariable UUID batchExternalId) {
        AccountEntity account = accountService.findByIssuerId(issuerId).get();
        BatchEntity batch = accountBatchService.getAccountBatch(account, batchExternalId).get();
        List<TransactionEntity> transactions = accountTransactionService.getTransactionsByAccountIdAndBatchId(account.getId(), batch.getId());

        return JsonResponse.set(HttpStatus.OK, "Operation successful.", transactions.stream().map(transactionDTOMapper).toList());
    }
}
