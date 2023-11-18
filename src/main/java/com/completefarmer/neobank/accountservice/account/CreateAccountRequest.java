package com.completefarmer.neobank.accountservice.account;

import com.completefarmer.neobank.accountservice.utils.validators.UniqueAccountEmail;
import com.completefarmer.neobank.accountservice.utils.validators.UniqueAccountPhoneNumber;
import com.completefarmer.neobank.accountservice.utils.validators.ValidEnumValue;
import com.completefarmer.neobank.accountservice.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO for incoming Create Account requests
 */
@Data
public class CreateAccountRequest {

    @NotNull(message = "name is required")
    @NotBlank(message = "name is required")
    String name;

    @NotNull(message = "type cannot be null")
    @ValidEnumValue(enumClass = AccountType.class, message = "account type is invalid")
    String type;

    @Email
    @UniqueAccountEmail
    String email = null;

    @UniqueAccountPhoneNumber
    @Pattern(regexp = "^0\\d{9}$", message = "must start with 0 and must be 10 digits")
    String phoneNumber;

}
