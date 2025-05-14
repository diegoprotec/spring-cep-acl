package com.santander.springcepapi.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.santander.springcepapi.util.CepUtil;
import jakarta.validation.executable.ValidateOnExecution;

@ValidateOnExecution
public record CepVo(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String estado,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String cepFormatado
) {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CepVo(
            String cep,
            String logradouro,
            String bairro,
            String localidade,
            String estado
    ) {
        this(CepUtil.getNormalizado(cep), logradouro, bairro, localidade, estado, CepUtil.getFormatado(cep));
    }

}

