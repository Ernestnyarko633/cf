package com.completefarmer.neobank.accountservice.batch;

import com.completefarmer.neobank.accountservice.accounttransaction.CreateTransactionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Batch request class
 * Encapsulates validation rules for new Batch requests
 */
@Data
@Getter
@Setter
public class CreateBatchRequest {
    @NotNull(message = "name is required")
    public String name;

    @NotNull(message = "description is required")
    public String description;

    @FutureOrPresent(message = "process at must be present or future data")
    public LocalDateTime processAt;

    @NotNull(message = "transactions is required")
    @Valid
    public List<CreateTransactionRequest> transactions;
}
