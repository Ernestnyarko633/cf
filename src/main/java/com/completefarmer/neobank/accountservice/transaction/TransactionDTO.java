package com.completefarmer.neobank.accountservice.transaction;

import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.enums.ETransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TransactionDTO maps transaction object to the properties specified in the class
 * This class helps to expose specific properties to the client
 *
 * @param externalId
 * @param amount
 * @param narration
 * @param clientReference
 * @param accountIssuer
 * @param accountNumber
 * @param accountName
 * @param callbackUrl
 * @param processAt
 * @param type
 * @param status
 * @param createdAt
 * @param updatedAt
 * @param balanceBefore
 * @param balanceAfter
 */
public record TransactionDTO(
        UUID externalId,
        Long amount,
        String narration,
        String clientReference,
        String accountIssuer,
        String accountNumber,
        String accountName,
        String callbackUrl,
        LocalDateTime processAt,
        ETransactionType type,
        ETransactionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long balanceBefore,
        Long balanceAfter,
        String initiatorName
) {
}
