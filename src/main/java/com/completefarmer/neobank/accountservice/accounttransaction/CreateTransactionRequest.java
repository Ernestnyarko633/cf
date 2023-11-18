package com.completefarmer.neobank.accountservice.accounttransaction;

import com.completefarmer.neobank.accountservice.batch.BatchEntity;
import com.completefarmer.neobank.accountservice.enums.ECurrency;
import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.enums.ETransactionType;
import com.completefarmer.neobank.accountservice.utils.validators.FixedLength;
import com.completefarmer.neobank.accountservice.utils.validators.ValidEnumValue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

/**
 * New transaction request DTO and validator
 */
@Data
public class CreateTransactionRequest {
    @NotNull(message = "amount is required")
    private Long amount;

    @NotNull(message = "narration is required")
    private String narration;

    @NotNull(message = "client reference is required")
    private String clientReference;

    @NotNull(message = "account issuer is required")
    @FixedLength(value = 3, message = "account issuer must be 3 characters long")
    private String accountIssuer;

    @NotNull(message = "account number is required")
    @Length(min = 10, max = 15, message = "account number must be 10 to 15 characters long")
    private String accountNumber;

    @NotNull(message = "account name is required")
    private String accountName;

    @URL
    private String callbackUrl;

    @FutureOrPresent(message = "processAt must be a present or a future date")
    private LocalDateTime processAt;

    @NotNull(message = "type is required")
    @ValidEnumValue(enumClass = ETransactionType.class, message = "the selected type is invalid")
    private String type;

    @Getter
    private Long balanceBefore;

    @Getter
    private Long balanceAfter;

    @Getter
    @Setter
    private BatchEntity batch;

    @Setter
    private ETransactionStatus status;

    @Getter
    private ECurrency currency;


    public ETransactionStatus getStatus() {
        return status == null ? ETransactionStatus.QUEUED : status;
    }

    /**
     * Converts valid user input to ETransactionType Enum value
     * @return ETransactionType Enum value
     */
    public ETransactionType getType() {
        return ETransactionType.valueOf(type.toUpperCase());
    }

    public void setCurrency(ECurrency currency) {
        this.currency = (currency ==  null) ? ECurrency.GHS : currency;
    }

}
