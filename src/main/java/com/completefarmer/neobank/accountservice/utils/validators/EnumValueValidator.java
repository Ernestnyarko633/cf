package com.completefarmer.neobank.accountservice.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * Enum Validation class
 * Checks given user input against specific enum values
 *
 * @author appiersign
 */
public class EnumValueValidator implements ConstraintValidator<ValidEnumValue, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnumValue constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value.equals("null") || value == null) return true; // Let other validators handle null values

        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null) {
            return false; // Not an enum class
        }

        return Arrays.stream(enumConstants)
                .map(Enum::name)
                .anyMatch(enumValue -> enumValue.equals(value));
    }
}
