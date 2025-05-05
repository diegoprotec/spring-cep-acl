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
    String message() default "CEP a ser pesquisado (formato: 00000000)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
