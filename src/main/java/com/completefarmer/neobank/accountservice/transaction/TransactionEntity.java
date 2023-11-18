package com.completefarmer.neobank.accountservice.transaction;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.account.TransactionEntityListener;
import com.completefarmer.neobank.accountservice.accounttransaction.AccountTransactionService;
import com.completefarmer.neobank.accountservice.accounttransaction.CreateTransactionRequest;
import com.completefarmer.neobank.accountservice.batch.BatchEntity;
import com.completefarmer.neobank.accountservice.enums.EAccountIssuers;
import com.completefarmer.neobank.accountservice.enums.ECurrency;
import com.completefarmer.neobank.accountservice.enums.ETransactionType;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * TransactionEntity
 * Entity representation of the Transaction model
 */
@Data
@Table(
        name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"externalId"},
                        name = "transactions_external_id_unique"
                )
        }
)
@Entity
@EqualsAndHashCode(callSuper = true)
@EntityListeners(TransactionEntityListener.class)
public class TransactionEntity extends BaseEntity {

    @JsonIgnore
    @ManyToOne(targetEntity = AccountEntity.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "accountId", nullable = false)
    private AccountEntity account;

    @Column(nullable = false, updatable = false)
    private Long amount;

    @Column(nullable = false, updatable = false)
    private String narration;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ETransactionType type;

    private String callbackUrl;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ETransactionStatus status;

    @Column(nullable = false)
    private Long balanceBefore;

    @Column(nullable = false)
    private Long balanceAfter;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private EAccountIssuers accountIssuer;

    @Column(nullable = false)
    private String accountName;

    private String initiatorName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ECurrency currency = ECurrency.GHS;

    @Getter
    @Setter
    @ManyToOne(targetEntity = BatchEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    @JsonIgnore
    private BatchEntity batch;

    @Getter
    @Column(nullable = false)
    private String clientReference;

    @Getter
    private LocalDateTime processAt;

    public TransactionEntity(
            Long amount,
            String narration,
            ETransactionType type,
            String callbackUrl,
            String accountNumber,
            String accountIssuer,
            String clientReference,
            Long balanceBefore,
            Long balanceAfter,
            LocalDateTime processAt
    ) {
        this.amount = amount;
        this.narration = narration;
        this.type = type;
        this.callbackUrl = callbackUrl;
        this.accountNumber = accountNumber;
        this.accountIssuer = EAccountIssuers.valueOf(accountIssuer);
        this.clientReference = clientReference;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.processAt = processAt;
    }

    public TransactionEntity(
            Long amount,
            String narration,
            ETransactionType type,
            String callbackUrl,
            String accountNumber,
            String accountIssuer,
            String clientReference,
            Long balanceBefore,
            Long balanceAfter,
            LocalDateTime processAt,
            BatchEntity batch
    ) {
        this.amount = amount;
        this.narration = narration;
        this.type = type;
        this.callbackUrl = callbackUrl;
        this.accountNumber = accountNumber;
        this.accountIssuer = EAccountIssuers.valueOf(accountIssuer);
        this.clientReference = clientReference;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.processAt = processAt;
        this.batch = batch;
    }

    public TransactionEntity(
            Long amount,
            String narration,
            ETransactionType type,
            String callbackUrl,
            ETransactionStatus status,
            String accountNumber,
            String accountIssuer
    ) {
        this.amount = amount;
        this.narration = narration;
        this.type = type;
        this.callbackUrl = callbackUrl;
        this.status = status;
        this.accountNumber = accountNumber;
        this.accountIssuer = EAccountIssuers.valueOf(accountIssuer);
    }

    public TransactionEntity() {

    }

    public TransactionEntity(
            Long amount,
            String narration,
            ETransactionType type,
            String callbackUrl,
            String accountNumber,
            String accountIssuer,
            String accountName,
            String clientReference,
            Long balanceBefore,
            Long balanceAfter,
            LocalDateTime processAt,
            BatchEntity batch
    ) {
        this.amount = amount;
        this.narration = narration;
        this.type = type;
        this.callbackUrl = callbackUrl;
        this.accountNumber = accountNumber;
        this.accountIssuer = EAccountIssuers.valueOf(accountIssuer);
        this.accountName = accountName;
        this.clientReference = clientReference;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.processAt = processAt;
        this.batch = batch;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public void setCurrency(ECurrency currency) {
        this.currency = currency == null ? ECurrency.GHS : currency;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public void setType(ETransactionType type) {
        this.type = type;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public void setStatus(ETransactionStatus status) {
        this.status = status;
    }

    public void setBalanceBefore(Long balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public void setBalanceAfter(Long balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountIssuer(String accountIssuer) {
        this.accountIssuer = EAccountIssuers.valueOf(accountIssuer);
    }

    public void setProcessAt(LocalDateTime processAt) {
        this.processAt = processAt;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = clientReference;
    }

    public Long getAmount() {
        return amount > 0 ? amount : amount * -1;
    }

    public boolean isDisbursement() {
        return type.equals(ETransactionType.DISBURSEMENT);
    }

    public boolean isCollection() {
        return type.equals(ETransactionType.COLLECTION);
    }

    @Override
    public void prePersist() {
        super.prePersist();
        if (isDisbursement()) {
            setAmount(getAmount() * -1);
        }
    }

    public boolean isQueued() {
        return this.status.equals(ETransactionStatus.QUEUED);
    }

    public boolean isCompleted() {
        return status == ETransactionStatus.COMPLETED;
    }

    public double getAmountInMajorUnits() {
        double balance = (amount.doubleValue() / 100);
        return balance > 0 ? balance : balance * -1;
    }

    public String getCallbackUrlParams() {
        return callbackUrl + "?status=" + status.toString().toLowerCase() + "&amount=" + amount +
                "&clientReference=" + clientReference + "&externalId=" + getExternalId();
    }
}
