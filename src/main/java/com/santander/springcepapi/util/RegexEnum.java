package com.santander.springcepapi.util;

public enum RegexEnum {

    APENAS_NUMEROS("[^0-9]");

    private final String value;

    RegexEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
