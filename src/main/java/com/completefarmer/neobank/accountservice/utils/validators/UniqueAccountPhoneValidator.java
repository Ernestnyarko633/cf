package com.completefarmer.neobank.accountservice.utils.validators;

import com.completefarmer.neobank.accountservice.account.AccountService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * UniqueAccountPhoneNumber Validation class
 * Ensures user's phone number us not already associated with an account
 * @author appiersign
 */
@Component
public class UniqueAccountPhoneValidator implements ConstraintValidator<UniqueAccountPhoneNumber, String> {

    @Autowired
    AccountService accountService;

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        return accountService.findByPhoneNumber(phoneNumber).isEmpty();
    }
}
