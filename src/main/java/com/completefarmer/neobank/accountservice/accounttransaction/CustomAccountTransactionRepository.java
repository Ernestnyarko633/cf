package com.completefarmer.neobank.accountservice.accounttransaction;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.enums.ETransactionType;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This class handles methods that run custom queries against an account and its transactions
 */

@Repository
public class CustomAccountTransactionRepository {
    private static final int MINUS_ONE = -1;


    private final EntityManager entityManager;

    @Autowired
    public CustomAccountTransactionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Query account actual balance
     * This is balance calculated from transactions that have been processed successfully
     *
     * @param account to get balance for
     * @return account balance
     */
    public Long getActualBalance(AccountEntity account) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT SUM(t.amount) " +
                        "FROM TransactionEntity t " +
                        "WHERE t.account = :account AND t.status IN :statuses",
                Long.class
        );

        query.setParameter("account", account);
        query.setParameter("statuses", List.of(
                ETransactionStatus.COMPLETED,
                ETransactionStatus.QUEUED,
                ETransactionStatus.INITIATED
        ));

        Long balance = query.getSingleResult();
        return balance == null ? 0 : balance;
    }

    /**
     * Query account available balance
     * this is balance against which new transactions are performed
     * only failed transactions are not calculated
     *
     * @param account to get balance for
     * @return Long available account balance
     */
    public Long getAvailableBalance(AccountEntity account) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT SUM(t.amount) " +
                        "FROM TransactionEntity t " +
                        "WHERE t.account = :account AND t.status IN :statuses",
                Long.class
        );
        query.setParameter("account", account);
        query.setParameter("statuses", List.of(ETransactionStatus.COMPLETED));
        Long balance = query.getSingleResult();
        return balance == null ? 0 : balance;
    }

    public long getTotalTransactionCount(AccountEntity account) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(t.id) from TransactionEntity t where t.account = :account", Long.class
        );
        query.setParameter("account", account);
        Long count = query.getSingleResult();
        return count == null ? 0 : count;
    }

    public long getTotalCollectionCount(AccountEntity account) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(t.id) from TransactionEntity t where t.type = :type and t.account = :account", Long.class
        );
        query.setParameter("account", account);
        query.setParameter("type", ETransactionType.COLLECTION);
        Long count = query.getSingleResult();
        return count == null ? 0 : count;
    }

    public long getTotalDisbursementCount(AccountEntity account) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(t.id) from TransactionEntity t where t.type = :type and t.account = :account", Long.class
        );
        query.setParameter("account", account);
        query.setParameter("type", ETransactionType.DISBURSEMENT);
        Long count = query.getSingleResult();
        return count == null ? 0 : count;
    }

    public long getTotalCollectionValue(AccountEntity account) {
        TypedQuery<Long> query = entityManager.createQuery(
                "select sum (t.amount) from TransactionEntity t " +
                        "where t.type = :type and t.account = :account and t.status = :status",
                Long.class
        );
        query.setParameter("account", account);
        query.setParameter("type", ETransactionType.COLLECTION);
        query.setParameter("status", ETransactionStatus.COMPLETED);
        Long sum = query.getSingleResult();
        return sum == null ? 0 : sum;
    }

    public long getTotalDisbursementValue(AccountEntity account) {
        TypedQuery<Long> query = entityManager.createQuery(
                "select sum (t.amount) from TransactionEntity t " +
                        "where t.type = :type and t.account = :account and t.status = :status",
                Long.class
        );
        query.setParameter("account", account);
        query.setParameter("type", ETransactionType.DISBURSEMENT);
        query.setParameter("status", ETransactionStatus.COMPLETED);
        Long sum = query.getSingleResult();
        return sum == null ? 0 : sum * MINUS_ONE;
    }

    public Optional<TransactionEntity> getLatestSuccessfulTransaction(AccountEntity account) {
        TypedQuery<TransactionEntity> query = entityManager.createQuery("SELECT t FROM TransactionEntity t " +
                "WHERE t.account = :account and t.status = :status order by t.updatedAt desc limit 1",
                TransactionEntity.class);
        query.setParameter("account", account).setParameter("status", ETransactionStatus.COMPLETED);
        return Optional.ofNullable(query.getSingleResult());
    }
}
