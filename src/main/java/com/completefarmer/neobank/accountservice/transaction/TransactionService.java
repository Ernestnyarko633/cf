package com.completefarmer.neobank.accountservice.transaction;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.account.AccountService;
import com.completefarmer.neobank.accountservice.accounttransaction.CustomAccountTransactionRepository;
import com.completefarmer.neobank.accountservice.enums.EAccountIssuers;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.enums.ETransactionType;
import com.completefarmer.neobank.accountservice.utils.exceptions.TransactionCannotBeCancelledException;
import com.completefarmer.neobank.accountservice.utils.microservices.apigateway.APIGatewayService;
import com.completefarmer.neobank.accountservice.utils.microservices.apigateway.CollectionRequest;
import com.completefarmer.neobank.accountservice.utils.microservices.apigateway.CollectionResponse;
import com.completefarmer.neobank.accountservice.utils.microservices.apigateway.DisbursementRequest;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TransactionService {
    public static final String NEO = "NEO";
    public static final String WEBHOOKS_TRANSACTIONS = "/webhooks/transactions";

    @Value("${disbursement.routing.key}")
    private String disbursementRoutingKey;

    @Value("${transaction.exchange}")
    private String transactionExchange;

    @Value("${app.url}")
    private String baseUrl;

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final CustomAccountTransactionRepository customAccountTransactionRepository;
    private final RabbitTemplate template;
    private final APIGatewayService apiGatewayService;
    private final Gson gson;

    @Autowired
    public TransactionService(
            AccountService accountService,
            TransactionRepository transactionRepository,
            CustomAccountTransactionRepository customAccountTransactionRepository,
            RabbitTemplate template,
            APIGatewayService apiGatewayService, Gson gson) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.customAccountTransactionRepository = customAccountTransactionRepository;
        this.template = template;
        this.apiGatewayService = apiGatewayService;
        this.gson = gson;
    }

    /**
     * Save new transaction
     *
     * @param account     performing the transaction
     * @param transaction details
     * @return TransactionEntity
     */
    public TransactionEntity save(AccountEntity account, TransactionEntity transaction) {
        transaction.setInitiatorName(account.getName());
        transaction.setBalanceBefore(customAccountTransactionRepository.getAvailableBalance(account));
        transaction.setBalanceAfter(account.getBalanceAfter(transaction.getAmount(), transaction.getType()));
        transaction.setAccount(account);

        log.info("Account " + account + " performing transaction " + transaction);
        transaction = transactionRepository.save(transaction);
        updateStatus(account, transaction, transaction.getStatus() != null ? transaction.getStatus() : ETransactionStatus.QUEUED);

        if (transaction.isDisbursement() && transaction.isQueued()) {
            transferFunds(account, transaction);
        }

        return transaction;
    }

    public CollectionResponse collectFunds(AccountEntity account, TransactionEntity transaction) {
        HttpResponse<String> response = apiGatewayService.sendCollectionRequest(
                new CollectionRequest(
                        transaction.getAmount(),
                        transaction.getCurrency().toString(),
                        "solomon.appier-sign@completefarmer.com",
                        transaction.getExternalId(),
                        transaction.getNarration(),
                        baseUrl + WEBHOOKS_TRANSACTIONS
                )
        );

        return (response != null) ? gson.fromJson(response.body(), CollectionResponse.class) : null;
    }

    private void transferFunds(AccountEntity account, TransactionEntity transaction) {
        if (transaction.getAccountIssuer().equals(EAccountIssuers.NEO)) {
            Optional<AccountEntity> _account = accountService.findByIssuerId(transaction.getAccountNumber());
            _account.ifPresent(__account -> {
                long availableBalance = customAccountTransactionRepository.getAvailableBalance(__account);
                TransactionEntity _transaction = new TransactionEntity(transaction.getAmount(),
                        transaction.getNarration(), ETransactionType.COLLECTION, "", account.getIssuerId(),
                        NEO, transaction.getExternalId().toString(), availableBalance,
                        availableBalance + transaction.getAmount(), null);

                _transaction.setAccountName(account.getName());
                _transaction.setStatus(ETransactionStatus.INITIATED);

                save(__account, _transaction);
                updateStatus(__account, _transaction, ETransactionStatus.COMPLETED);
                updateStatus(account, transaction, ETransactionStatus.COMPLETED);
            });
        } else {
            // TODO: Queue the transaction for external transfer by API Gateway
            transaction.setStatus(ETransactionStatus.INITIATED);
            save(account, transaction);
            transaction.setCreatedAt(null);
            transaction.setUpdatedAt(null);
            transaction.setProcessAt(null);
            transaction.setCallbackUrl(baseUrl + WEBHOOKS_TRANSACTIONS);
            DisbursementRequest disbursementRequest = new DisbursementRequest(transaction.getAccountNumber(),
                    transaction.getAccountIssuer().toString(), transaction.getAmount(), transaction.getExternalId().toString(),
                    transaction.getCallbackUrl(), transaction.getNarration(), transaction.getCurrency().toString());
            template.convertAndSend(transactionExchange, disbursementRoutingKey, disbursementRequest);
        }
    }

    private void updateAccountBalance(AccountEntity account) {
        Long availableBalance = account.getAvailableBalance();
        account.setAvailableBalance(customAccountTransactionRepository.getAvailableBalance(account));
        account.setActualBalance(customAccountTransactionRepository.getActualBalance(account));
        accountService.update(account, availableBalance);
    }

    /**
     * Find transaction by transaction UUID
     *
     * @param transactionId UUID
     * @return TransactionEntity if match found
     */
    public TransactionEntity findByExternalId(UUID transactionId) {
        return transactionRepository.findTransactionEntityByExternalId(transactionId).get();
    }

    /**
     * Updates transaction status
     *
     * @param transaction to update
     * @param status      to update
     */
    public void updateStatus(AccountEntity account, TransactionEntity transaction, ETransactionStatus status) {
        transaction.setStatus(status);
        transactionRepository.save(transaction);
        updateAccountBalance(account);
    }

    /**
     * FindAll query that uses predefined specification or filters
     *
     * @param specification to use to filter, sort
     * @param pageable      for pagination
     * @return paginated list of transactions
     */
    public Page<TransactionEntity> findAll(Specification<TransactionEntity> specification, Pageable pageable) {
        return transactionRepository.findAll(specification, pageable);
    }

    /**
     * Cancels a scheduled transaction
     *
     * @param transaction to be cancelled
     * @throws TransactionCannotBeCancelledException if status of the transaction is not QUEUED
     */
    public void cancelScheduledTransaction(TransactionEntity transaction) {
        if (!transaction.isQueued()) {
            throw new TransactionCannotBeCancelledException("Transaction with status " + transaction.getStatus().toString() + " cannot be cancelled.");
        }

        transaction.setStatus(ETransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
    }
}
