package com.completefarmer.neobank.accountservice.accounttransaction;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Account Transaction Related queries
 */
@Repository
public interface AccountTransactionRepository extends JpaRepository<TransactionEntity, String> {
    List<TransactionEntity> findAllByAccountId(Long id);

    TransactionEntity findAllByAccountIdAndExternalId(Long accountId, UUID transactionId);

    List<TransactionEntity> findAllByAccountIdAndBatchId(Long accountId, Long batchId);

    TransactionEntity findByAccountIdAndStatus(Long accountId, ETransactionStatus status);

    TransactionEntity findTopByAccountIdAndStatusEquals(Long accountId, ETransactionStatus status);
}
