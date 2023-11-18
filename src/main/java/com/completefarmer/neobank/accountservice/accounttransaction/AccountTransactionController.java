package com.completefarmer.neobank.accountservice.accounttransaction;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.account.AccountService;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.enums.ETransactionType;
import com.completefarmer.neobank.accountservice.transaction.TransactionDTOMapper;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import com.completefarmer.neobank.accountservice.transaction.TransactionService;
import com.completefarmer.neobank.accountservice.utils.exceptions.BadTransactionCandidateException;
import com.completefarmer.neobank.accountservice.utils.exceptions.BadTransactionStatusException;
import com.completefarmer.neobank.accountservice.utils.microservices.apigateway.CollectionResponse;
import com.completefarmer.neobank.accountservice.utils.responses.JsonResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Account Transactions Controller
 * This class handles Http requests made to /account/:accountId/transactions....
 *
 * @author appiersign
 */

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
public class AccountTransactionController {
    private final CustomAccountTransactionRepository customAccountTransactionRepository;
    private final AccountTransactionService accountTransactionService;
    private final TransactionDTOMapper transactionDTOMapper;
    private final TransactionService transactionService;
    private final AccountService accountService;

    @Autowired
    public AccountTransactionController(
            CustomAccountTransactionRepository customAccountTransactionRepository,
            AccountTransactionService accountTransactionService,
            TransactionDTOMapper transactionDTOMapper,
            TransactionService transactionService,
            AccountService accountService
    ) {
        this.customAccountTransactionRepository = customAccountTransactionRepository;
        this.accountTransactionService = accountTransactionService;
        this.transactionDTOMapper = transactionDTOMapper;
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    /**
     * Get All Transactions associated with an account using the account number [issuerId]
     *
     * @param issuerId Neobank account number
     * @return ResponseEntity
     */
    @GetMapping("/{issuerId}/transactions")
    public ResponseEntity<?> getTransactions(
            @PathVariable String issuerId,
            @RequestParam(name = "start-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(name = "end-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "rows", required = false, defaultValue = "10") Integer rows
    ) {
        log.info("Get Account Transaction request received: " + issuerId);
        AccountEntity account = accountService.findByIssuerId(issuerId).get();

        Specification<TransactionEntity> specification = getAccountSpecification(account);

        if (startDate != null) specification = getStartDateSpecification(startDate, specification);
        if (endDate != null) specification = getEndDateSpecification(endDate, specification);
        if (type != null && !type.isEmpty()) specification = getTypeSpecification(type, specification);
        if (status != null && !status.isEmpty()) specification = getStatusSpecification(status, specification);

        log.info("Account holder retrieved: {}", account);
        Pageable pageable = PageRequest.of(0, rows);
        Page<TransactionEntity> transactions = transactionService.findAll(specification, pageable);

        Map<String, Object> paginatorData = transactionMap(transactions);

        return JsonResponse.set(
                HttpStatus.OK,
                "Transactions retrieved",
                paginatorData
        );
    }

    /**
     * Query specification that filters by account
     *
     * @param account that performed the transactions
     * @return specification
     */
    private static Specification<TransactionEntity> getAccountSpecification(AccountEntity account) {
        return Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("account"), account
        ));
    }

    /**
     * Query specification that filter transactions by status
     *
     * @param status        of the transactions
     * @param specification initially defined
     * @return updated specification
     */
    private static Specification<TransactionEntity> getStatusSpecification(String status, Specification<TransactionEntity> specification) {
        return specification.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), ETransactionStatus.valueOf(status.toUpperCase()))
        );
    }

    /**
     * Query specification that filter transactions by type
     *
     * @param type          of the transactions
     * @param specification initially defined
     * @return updated specification
     */
    private static Specification<TransactionEntity> getTypeSpecification(String type, Specification<TransactionEntity> specification) {
        return specification.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), ETransactionType.valueOf(type.toUpperCase()))
        );
    }

    /**
     * Query specification that filter transactions by createdAt
     *
     * @param endDate       of the transactions
     * @param specification initially defined
     * @return updated specification
     */
    private static Specification<TransactionEntity> getEndDateSpecification(LocalDateTime endDate, Specification<TransactionEntity> specification) {
        return specification.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate)
        );
    }

    /**
     * Query specification that filter transactions by createdAt
     *
     * @param startDate     of the transactions
     * @param specification initially defined
     * @return updated specification
     */
    private static Specification<TransactionEntity> getStartDateSpecification(LocalDateTime startDate, Specification<TransactionEntity> specification) {
        return specification.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate)
        );
    }

    /**
     * Response mapper that return a paginated map response and TransactionDTO
     *
     * @param transactions returned from query
     * @return a map
     */
    private Map<String, Object> transactionMap(Page<TransactionEntity> transactions) {
        Map<String, Object> paginatorData = new HashMap<>();
        Map<String, Object> pagination = new HashMap<>();

        pagination.put("totalElements", transactions.getTotalElements());
        pagination.put("totalPages", transactions.getTotalPages());
        pagination.put("size", transactions.getSize());
        pagination.put("sorting", transactions.getSort());
        pagination.put("lastPage", transactions.isLast());
        pagination.put("firstPage", transactions.isFirst());

        paginatorData.put(
                "transactions",
                transactions.getContent()
                        .stream()
                        .map(transactionDTOMapper)
                        .collect(Collectors.toList())
        );
        paginatorData.put("meta", transactions.getPageable());
        paginatorData.put("pagination", pagination);
        return paginatorData;
    }

    /**
     * Get Single Transaction details
     *
     * @param issuerId      Neobank Account number
     * @param transactionId transaction UUID
     * @return ResponseEntity
     */
    @GetMapping("/{issuerId}/transactions/{transactionId}")
    public ResponseEntity<?> getTransactions(@PathVariable String issuerId, @PathVariable String transactionId) {
        TransactionEntity transaction = accountTransactionService.getAccountTransaction(
                getAccountByIssuerId(issuerId),
                transactionId
        );

        return JsonResponse.set(
                HttpStatus.OK,
                "Transaction fetched successfully",
                transactionDTOMapper.apply(transaction)
        );
    }

    /**
     * Create new Transaction request handler
     *
     * @param request  transaction request DTO
     * @param issuerId neobank account number
     * @return TransactionEntity
     */
    @PostMapping("/{issuerId}/transactions")
    public ResponseEntity<?> createTransaction(
            @RequestBody @Valid CreateTransactionRequest request,
            @PathVariable String issuerId
    ) {
        log.info("New Transaction request received: " + request);

        AccountEntity account = getAccountByIssuerId(issuerId);
        TransactionEntity transaction = new TransactionEntity(
                request.getAmount(),
                request.getNarration(),
                request.getType(),
                request.getCallbackUrl(),
                request.getAccountNumber(),
                request.getAccountIssuer(),
                request.getAccountName(),
                request.getClientReference(),
                request.getBalanceBefore(),
                request.getBalanceAfter(),
                request.getProcessAt(),
                request.getBatch()
        );

        transaction.setCurrency(request.getCurrency());

        accountTransactionService.checkAccountBalance(account, transaction);

        if (request.getStatus() != null) transaction.setStatus(request.getStatus());

        transaction = transactionService.save(account, transaction);

        return JsonResponse.set(HttpStatus.ACCEPTED, "Transaction queued for processing",
                transactionDTOMapper.apply(transaction));
    }

    /**
     * Update Transaction status handler
     *
     * @param request       update transaction request
     * @param issuerId      neobank account number
     * @param transactionId transaction UUID
     * @return TransactionEntity
     */
    @PutMapping("/{issuerId}/transactions/{transactionId}")
    public ResponseEntity<?> updateTransaction(
            @RequestBody @Valid UpdateTransactionStatusRequest request,
            @PathVariable String issuerId,
            @PathVariable UUID transactionId
    ) {
        log.info("New Transaction request received: " + request);

        AccountEntity account = getAccountByIssuerId(issuerId);
        TransactionEntity transaction = transactionService.findByExternalId(transactionId);

        transactionService.updateStatus(account, transaction, ETransactionStatus.valueOf(request.getStatus()));

        return JsonResponse.set(
                HttpStatus.ACCEPTED,
                "Transaction updated",
                transactionDTOMapper.apply(transaction)
        );
    }

    /**
     * Cancel Scheduled Transaction status handler
     *
     * @param issuerId      neobank account number
     * @param transactionId transaction UUID
     * @return no content
     */
    @PutMapping("/{issuerId}/transactions/{transactionId}/cancel")
    public ResponseEntity<?> cancelScheduledTransaction(@PathVariable String issuerId, @PathVariable UUID transactionId) {
        log.info("New Cancel Scheduled Transaction request received");

        AccountEntity account = getAccountByIssuerId(issuerId);
        TransactionEntity transaction = transactionService.findByExternalId(transactionId);
        transactionService.cancelScheduledTransaction(transaction);

        Long availableBalance = customAccountTransactionRepository.getAvailableBalance(account);
        account.setAvailableBalance(availableBalance);
        account.setActualBalance(customAccountTransactionRepository.getActualBalance(account));

        accountService.update(account, availableBalance);

        return JsonResponse.set(HttpStatus.NO_CONTENT, "Transaction cancelled", new String[]{});
    }

    @GetMapping("/{issuerId}/transactions/{transactionId}/payment-link")
    public ResponseEntity<?> getPaymentLink(
            @PathVariable String issuerId,
            @PathVariable String transactionId
    ) {
        AccountEntity account = accountService.findByIssuerId(issuerId).get();
        CollectionResponse response = generatePaymentLink(transactionId, account);
        return JsonResponse.set(HttpStatus.OK, "Payment link generated", Map.of("url", response.data().paymentUrl()));
    }

    private CollectionResponse generatePaymentLink(String transactionId, AccountEntity account) {
        TransactionEntity transaction = accountTransactionService.getAccountTransaction(account, transactionId);

        if (transaction == null) throw new NoSuchElementException("Transaction not found");

        if (!transaction.isCollection()) {
            throw new BadTransactionCandidateException(
                    "Payment link for transaction type: " + transaction.getType().toString() + " is" +
                            " not allowed"
            );
        }

        if (!transaction.isQueued()) throw new BadTransactionStatusException("Payment link generation for " +
                "transaction status: " + transaction.getStatus().toString() + " is not allowed.");

        CollectionResponse response = transactionService.collectFunds(account, transaction);

//        if (response.data()) {
            transactionService.updateStatus(account, transaction, ETransactionStatus.INITIATED);
//        }

        return response;
    }


    /**
     * Fetch Account by issuer ID/Account number
     *
     * @param issuerId neobank account number
     * @return AccountEntity
     */
    private AccountEntity getAccountByIssuerId(String issuerId) {
        return accountService.findByIssuerId(issuerId).get();
    }
}
