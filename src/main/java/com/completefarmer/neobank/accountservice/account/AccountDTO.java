package com.completefarmer.neobank.accountservice.account;

import com.completefarmer.neobank.accountservice.enums.AccountStatus;
import com.completefarmer.neobank.accountservice.enums.AccountType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Account response
 * @param externalId
 * @param name
 * @param issuerId
 * @param type
 * @param status
 * @param phoneNumber
 * @param email
 * @param createdAt
 * @param updatedAt
 */
public record AccountDTO (
        UUID externalId,
        String name,
        String issuerId,
        String type,
        AccountStatus status,
        String phoneNumber,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
