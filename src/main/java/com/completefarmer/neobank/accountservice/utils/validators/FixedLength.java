package com.completefarmer.neobank.accountservice.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Fixed length validator annotation
 * @author appiersign
 */

@Documented
@Constraint(validatedBy = FixedLengthValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FixedLength {
    String message() default "invalid length";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value();
}
