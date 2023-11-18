package com.completefarmer.neobank.accountservice.accounttransaction;

import com.completefarmer.neobank.accountservice.enums.ETransactionStatus;
import com.completefarmer.neobank.accountservice.utils.validators.ValidEnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Transaction status update RequestDTO and validator
 */
@Data
public class UpdateTransactionStatusRequest {
    @NotNull(message = "status is required")
    @ValidEnumValue(enumClass = ETransactionStatus.class, message = "the selected status is invalid")
    private String status;

    public ETransactionStatus getType() {
        return ETransactionStatus.valueOf(status.toUpperCase());
    }
}
