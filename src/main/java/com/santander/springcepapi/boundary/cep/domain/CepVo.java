package com.santander.springcepapi.boundary.cep.domain;

import com.santander.springcepapi.exception.constraint.cep.CepConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CepVo(
        @NotBlank(message = "O campo 'cep' é obrigatório.")
        @Size(min = 8, max = 8, message = "O CEP deve ter exatamente 8 caracteres.")
        @CepConstraint
        String cep,
        @NotBlank(message = "O campo 'logradouro' é obrigatório.")
        String logradouro,
        @NotBlank(message = "O campo 'bairro' é obrigatório.")
        String bairro,
        @NotBlank(message = "O campo 'localidade' é obrigatório.")
        String localidade,
        @NotBlank(message = "O campo 'estado' é obrigatório.")
        String estado
) {
    public CepVo(
            String cep,
            String logradouro,
            String bairro,
            String localidade,
            String estado) {
        if (cep == null) {
            throw new IllegalArgumentException("O campo 'cep' é obrigatório");
        }
        this.cep = cep.replaceAll("[^0-9]", "");
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.localidade = localidade;
        this.estado = estado;
    }
}