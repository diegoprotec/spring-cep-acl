package com.santander.springcepapi.exception.constraint.cep;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CepValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CepConstraint {
    String message() default "O 'cep' deve conter 8 dígitos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
