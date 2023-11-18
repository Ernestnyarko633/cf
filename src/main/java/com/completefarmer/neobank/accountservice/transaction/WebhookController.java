package com.completefarmer.neobank.accountservice.transaction;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/webhooks")
public class WebhookController {

    private final TransactionService transactionService;

    @Autowired
    public WebhookController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Transaction status callback handler
     * Receives incoming http GET request and updates transaction's status
     *
     * @param reference: transaction externalId
     * @param status:    updated status of transaction
     */
    @GetMapping("/transactions")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Object> transactionCallbackHandler(
            @RequestParam(value = "reference") String reference,
            @RequestParam(value = "status") String status,
            HttpServletResponse httpServletResponse
    ) {
        log.info("Transaction callback received: reference=" + reference + ", status=" + status);
        TransactionEntity transaction = transactionService.findByExternalId(UUID.fromString(reference));

        log.info("Transaction retrieved: {}", transaction);
        AccountEntity account = transaction.getAccount();

        transactionService.updateStatus(account, transaction, ETransactionStatus.valueOf(status.toUpperCase()));

        log.info("Redirecting to client callback url: {}", transaction.getCallbackUrlParams());
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(transaction.getCallbackUrlParams())).build();
    }
}
