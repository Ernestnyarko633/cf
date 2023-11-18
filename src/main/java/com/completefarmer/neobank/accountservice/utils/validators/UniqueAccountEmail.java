package com.completefarmer.neobank.accountservice.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UniqueAccountEmail Validation Annotation interface
 * Ensures user's email address us not already associated with an account
 * @author appiersign
 */
@Constraint(validatedBy = UniqueAccountEmailValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueAccountEmail {
    String message() default "email address is already registered";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}