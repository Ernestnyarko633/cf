package com.completefarmer.neobank.accountservice.transaction;

import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * This class maps transaction objects to fields in the TransactionDTO
 */
@Component
public class TransactionDTOMapper implements Function<TransactionEntity, TransactionDTO> {

    @Override
    public TransactionDTO apply(TransactionEntity transaction) {
        return new TransactionDTO(
                transaction.getExternalId(),
                transaction.getAmount(),
                transaction.getNarration(),
                transaction.getClientReference(),
                transaction.getAccountIssuer().toString(),
                transaction.getAccountNumber(),
                transaction.getAccountName(),
                transaction.getCallbackUrl(),
                transaction.getProcessAt(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt(),
                transaction.getBalanceBefore(),
                transaction.getBalanceAfter(),
                transaction.getInitiatorName()
        );
    }
}
