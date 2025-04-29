package com.santander.springcepapi.config.webflux;

import com.santander.springcepapi.boundary.cep.domain.CepVo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface
CepClient {

    @GetExchange("{cep}/json")
    CepVo buscaCep(@PathVariable String cep);

}
