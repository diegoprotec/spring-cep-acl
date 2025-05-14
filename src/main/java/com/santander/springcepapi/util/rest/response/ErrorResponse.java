package com.santander.springcepapi.util.rest.response;

public record ErrorResponse(
        boolean error,
        String mensagem) {
}