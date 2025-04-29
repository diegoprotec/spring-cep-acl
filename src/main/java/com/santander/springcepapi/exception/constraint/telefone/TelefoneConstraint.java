package com.santander.springcepapi.exception.constraint.telefone;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TelefoneValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TelefoneConstraint {
    String message() default "O 'telefone' deve conter 13 dígitos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
