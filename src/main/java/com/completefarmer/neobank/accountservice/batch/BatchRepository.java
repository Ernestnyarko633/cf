package com.completefarmer.neobank.accountservice.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Batch Repository interface that extends JPA's Repos
 */
@Repository
public interface BatchRepository extends JpaRepository<BatchEntity, UUID> {
}
