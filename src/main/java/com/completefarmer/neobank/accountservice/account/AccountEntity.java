package com.completefarmer.neobank.accountservice.account;

import com.completefarmer.neobank.accountservice.batch.BatchEntity;
import com.completefarmer.neobank.accountservice.enums.AccountStatus;
import com.completefarmer.neobank.accountservice.enums.AccountType;
import com.completefarmer.neobank.accountservice.enums.ENotificationChannels;
import com.completefarmer.neobank.accountservice.enums.ETransactionType;
import com.completefarmer.neobank.accountservice.notifications.NotificationRecipient;
import com.completefarmer.neobank.accountservice.transaction.TransactionEntity;
import com.completefarmer.neobank.accountservice.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Set;

/**
 * Account Entity model presentation
 *
 * @author appiersign
 */

@Getter
@Entity
@EntityListeners(value = {AccountEntityListener.class})
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = "externalId", name = "accounts_external_id_unique"),
        @UniqueConstraint(columnNames = "issuerId", name = "accounts_issuer_id_unique"),
})
public class AccountEntity extends BaseEntity implements NotificationRecipient {

    @Column(unique = true)
    private String issuerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email = null;

    @Column(nullable = false)
    private Long actualBalance;

    @Column(nullable = false)
    private Long availableBalance;

    @Column(nullable = false)
    private Long maxTransactionLimit;

    @OneToMany(mappedBy = "account")
    private Set<TransactionEntity> transactions;

    @OneToMany(mappedBy = "account")
    private Set<BatchEntity> batches;

    public AccountEntity() {
    }

    public AccountEntity(String name, String type, AccountStatus status, String phoneNumber, String email) {
        super();
        this.name = name;
        this.type = type;
        this.status = status;
        this.phoneNumber = phoneNumber;
        setEmail(email);
    }

    public AccountEntity(String name, String type, String phoneNumber, String email) {
        super();
        this.name = name;
        this.type = type;
        this.phoneNumber = phoneNumber;
        setEmail(email);
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.toLowerCase();
    }

    @Override
    public void prePersist() {
        super.prePersist();
        actualBalance = 0L;
        availableBalance = 0L;
        maxTransactionLimit = 0L;
        issuerId = phoneNumber;
        status = AccountStatus.ACTIVATED;
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "issuerId='" + issuerId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", actualBalance=" + actualBalance +
                ", availableBalance=" + availableBalance +
                '}';
    }

    public void setIssuerId(String s) {
        issuerId = s;
    }

    public void setAvailableBalance(Long availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void setActualBalance(Long actualBalance) {
        this.actualBalance = actualBalance;
    }

    public boolean hasSufficientBalance(Long amount) {
        return availableBalance < amount;
    }

    public Long getBalanceAfter(Long amount, ETransactionType transactionType) {
        return ETransactionType.DISBURSEMENT.equals(transactionType) ?
                getAvailableBalance() - amount :
                getAvailableBalance() + amount;
    }

    public boolean isCustomer() {
        return this.type.equalsIgnoreCase(AccountType.CUSTOMER.toString());
    }

    public boolean isMerchant() {
        return this.type.equalsIgnoreCase(AccountType.MERCHANT.toString());
    }

    public double getAvailableBalanceInMajorUnits() {
        double balance = availableBalance.doubleValue() / 100;
        return  (balance > 0) ? balance : balance * -1;
    }

    @Override
    public ENotificationChannels getNotificationChannel() {
        return email != null ? ENotificationChannels.EMAIL : ENotificationChannels.SMS;
    }

    @Override
    public String getNotificationAddress() {
        return email != null ? email : phoneNumber;
    }
}
