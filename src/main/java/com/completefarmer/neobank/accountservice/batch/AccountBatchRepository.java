package com.completefarmer.neobank.accountservice.batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountBatchRepository extends JpaRepository<BatchEntity, UUID> {

    List<BatchEntity> findAllByAccountId(Long accountId);

    Optional<BatchEntity> findByAccountIdAndExternalId(Long accountId, UUID batchExternalId);

    Optional<BatchEntity> findByExternalId(UUID externalId);
}
