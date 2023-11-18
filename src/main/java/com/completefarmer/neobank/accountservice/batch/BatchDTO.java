package com.completefarmer.neobank.accountservice.batch;

import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BatchEntity DTO
 * @param externalId
 * @param name
 * @param description
 * @param totalUniqueCount
 * @param totalDuplicateCount
 * @param totalSuccessfulCount
 * @param totalFailedCount
 * @param totalPendingCount
 * @param totalTransactionValue
 * @param totalTransactionCount
 * @param processAt
 * @param status
 * @param createdAt
 * @param updatedAt
 */
public record BatchDTO(
        UUID externalId,
        String name,
        String description,
        Integer totalUniqueCount,
        Integer totalDuplicateCount,
        Integer totalSuccessfulCount,
        Integer totalFailedCount,
        Integer totalPendingCount,
        Long totalTransactionValue,
        Integer totalTransactionCount,
        LocalDateTime processAt,
        ETransactionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
