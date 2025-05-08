package com.santander.springcepapi.exception.constraint.cep;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "O campo 'cep' é obrigatório.")
@Size(min = 8, max = 10, message = "O 'cep' deve conter de 8 e 10 caracteres.")
@Constraint(validatedBy = CepValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CepConstraint {
    String message() default "Formatos válidos de 'cep': 00.000-000, 00000-000, 00000000";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
