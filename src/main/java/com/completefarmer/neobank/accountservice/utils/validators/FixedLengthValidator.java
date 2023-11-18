package com.completefarmer.neobank.accountservice.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Fixed length validation class
 * Ensures the length of a value is of a specific size
 * @author appiersign
 */
public class FixedLengthValidator implements ConstraintValidator<FixedLength, Object> {

    private int value;
    private String message;

    @Override
    public void initialize(FixedLength constraintAnnotation) {
        value = constraintAnnotation.value();
        message = constraintAnnotation.message();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String valueToString = String.valueOf(o);
        if (valueToString.equals("null")) return true;
        boolean valid = valueToString.length() == value;
        if (!valid) {
            if (message.isEmpty()) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext
                        .buildConstraintViolationWithTemplate("must be " + value + " characters long")
                        .addConstraintViolation();
            }
        }

        return valid;
    }
}
