package com.completefarmer.neobank.accountservice.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    Page<TransactionEntity> findAll(Specification<TransactionEntity> specification, Pageable pageable);

    Optional<TransactionEntity> findTransactionEntityByExternalId(UUID transactionId);
}
