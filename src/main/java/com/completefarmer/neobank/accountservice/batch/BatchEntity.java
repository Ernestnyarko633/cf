package com.completefarmer.neobank.accountservice.batch;

import com.completefarmer.neobank.accountservice.account.AccountEntity;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import com.completefarmer.neobank.accountservice.utils.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.grammars.hql.HqlParser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Entity representation of a batch
 */
@Entity
@Getter
@Setter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "batches")
public class BatchEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private LocalDateTime processAt;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ETransactionStatus status;

    @ManyToOne(targetEntity = AccountEntity.class)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonIgnore
    private AccountEntity account;

    @OneToMany(mappedBy = "batch")
    @JsonIgnore
    private Set<TransactionEntity> transactions;

    @Column(nullable = false)
    private Integer totalUniqueCount = 0;

    @Column(nullable = false)
    private Integer totalDuplicateCount = 0;

    @Column(nullable = false)
    private Integer totalSuccessfulCount = 0;

    @Column(nullable = false)
    private Integer totalFailedCount = 0;

    @Column(nullable = false)
    private Integer totalPendingCount = 0;

    @Column(nullable = false)
    private Integer totalTransactionCount = 0;

    @Column(nullable = false)
    private Long totalTransactionValue = 0L;

    public BatchEntity(String name, String description, LocalDateTime processAt, AccountEntity account) {
        this.name = name;
        this.description = description;
        this.processAt = processAt;
        this.account = account;
    }

    public BatchEntity(String name, String description, LocalDateTime processAt, AccountEntity account, ETransactionStatus status) {
        this.name = name;
        this.description = description;
        this.processAt = processAt;
        this.account = account;
        this.status = status;
    }
}
