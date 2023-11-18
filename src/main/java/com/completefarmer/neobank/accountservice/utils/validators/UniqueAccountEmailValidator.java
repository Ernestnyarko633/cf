package com.completefarmer.neobank.accountservice.utils.validators;

import com.completefarmer.neobank.accountservice.account.AccountService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * UniqueAccountEmail Validation class
 * Ensures user's email address us not already associated with an account
 * @author appiersign
 */
@Component
public class UniqueAccountEmailValidator implements ConstraintValidator<UniqueAccountEmail, String> {

    @Autowired
    AccountService accountService;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || accountService.existsByEmail(s).isEmpty();
    }
}
