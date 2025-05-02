package com.santander.springcepapi.exception.constraint.telefone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class TelefoneValidator implements ConstraintValidator<TelefoneConstraint, String> {

    private static final String TELEFONE_REGEX = "\\d{13}";
    private static final Pattern TELEFONE_PATTERN = Pattern.compile(TELEFONE_REGEX);

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null) {
            return true;
        }
        return TELEFONE_PATTERN.matcher(telefone).matches();
    }
}
