package com.santander.springcepapi.boundary.cep.application;

import com.santander.springcepapi.boundary.cep.domain.CepVo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public interface CepApplication {

    List<CepVo> buscarTodos();

    void salvar(CepVo cep);

    CepVo buscar(
            @NotBlank(message = "O CEP é obrigatório")
            @Pattern(regexp = "\\d{8}", message = "CEP deve ter exatamente 8 dígitos")
            String cep);

}
