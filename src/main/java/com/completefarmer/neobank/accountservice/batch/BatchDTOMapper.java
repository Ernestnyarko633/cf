package com.completefarmer.neobank.accountservice.batch;

import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * BatchDTO enables the exposure of specific BatchEntity properties
 */
@Component
public class BatchDTOMapper implements Function<BatchEntity, BatchDTO> {

    @Override
    public BatchDTO apply(BatchEntity batch) {
        return new BatchDTO(
                batch.getExternalId(),
                batch.getName(),
                batch.getDescription(),
                batch.getTotalUniqueCount(),
                batch.getTotalDuplicateCount(),
                batch.getTotalSuccessfulCount(),
                batch.getTotalFailedCount(),
                batch.getTotalPendingCount(),
                batch.getTotalTransactionValue(),
                batch.getTotalTransactionCount(),
                batch.getProcessAt(),
                batch.getStatus(),
                batch.getCreatedAt(),
                batch.getUpdatedAt()
        );
    }
}
