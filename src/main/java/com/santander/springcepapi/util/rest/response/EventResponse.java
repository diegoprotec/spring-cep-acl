package com.santander.springcepapi.util.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventResponse<T>(
        T dados,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        ErrorResponse error,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String status,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime momento) {

    public static <T> EventResponse<T> ofSuccess(T dados) {
        return new EventResponse<>(
                dados,
                null,
                StatusEventResponse.SUCCESS.getValue(), LocalDateTime.now());
    }

    public static <T> EventResponse<T> ofError(String mensagem) {
        return new EventResponse<>(
                null,
                new ErrorResponse(true, mensagem),
                StatusEventResponse.ERROR.getValue(), LocalDateTime.now());
    }

}

