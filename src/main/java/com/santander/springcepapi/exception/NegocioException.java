package com.santander.springcepapi.exception;

public class NegocioException extends RuntimeException {

    public NegocioException(String message) {
        super(message);
    }

}
