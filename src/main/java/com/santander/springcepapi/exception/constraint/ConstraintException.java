package com.santander.springcepapi.exception.constraint;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class ConstraintException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Violações de restrições de validação encontradas";
    private final Set<ConstraintViolation<?>> violations;

    public ConstraintException(Set<? extends ConstraintViolation<?>> violations) {
        super(DEFAULT_MESSAGE);
        this.violations = Set.copyOf(violations);
    }

    public Set<ConstraintViolation<?>> getViolations() {
        return violations;
    }

    @Override
    public String toString() {
        return String.format("%s: %d violações encontradas", DEFAULT_MESSAGE, violations.size());
    }

}
