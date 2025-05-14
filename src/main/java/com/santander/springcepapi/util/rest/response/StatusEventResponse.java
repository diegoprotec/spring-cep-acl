package com.santander.springcepapi.util.rest.response;

public enum StatusEventResponse {

    SUCCESS("success"),
    ERROR("error");

    private final String value;

    StatusEventResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
