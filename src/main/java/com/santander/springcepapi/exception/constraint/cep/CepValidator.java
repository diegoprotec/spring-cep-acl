package com.santander.springcepapi.exception.constraint.cep;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class CepValidator implements ConstraintValidator<CepConstraint, String> {

    private static final String CEP_REGEX = "\\d{8}";
    private static final Pattern CEP_PATTERN = Pattern.compile(CEP_REGEX);

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null) return true;
        return CEP_PATTERN.matcher(cep).matches();
    }
}
