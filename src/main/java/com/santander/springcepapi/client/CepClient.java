package com.santander.springcepapi.client;

import com.santander.springcepapi.model.vo.CepVo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange
public interface
CepClient {

    @GetExchange("/{cep}/json")
    Mono<CepVo> buscaCep(@PathVariable String cep);

}
