package com.completefarmer.neobank.accountservice.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UniqueAccountPhoneNumber Validation Annotation interface
 * Ensures user's phone number us not already associated with an account
 * @author appiersign
 */
@Constraint(validatedBy = UniqueAccountPhoneValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueAccountPhoneNumber {
    String message() default "phone number is already registered";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}