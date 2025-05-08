package com.santander.springcepapi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class CepClientFilter implements ExchangeFilterFunction {

    private static final Logger LOG = LoggerFactory.getLogger(CepClientFilter.class);
    private static final String ERRO_PADRAO = "\"erro\"";

    @Override
    @NonNull
    public Mono<ClientResponse> filter(@NonNull ClientRequest request, ExchangeFunction next) {
        return next.exchange(request)
                .flatMap(this::processarResposta);
    }

    private Mono<ClientResponse> processarResposta(ClientResponse response) {
        LOG.info("Status da resposta HTTP: {}", response.statusCode().value());
        if (!response.statusCode().is2xxSuccessful()) {
            return response.createError();
        }

        return response
                .bodyToMono(String.class)
                .flatMap(this::validarConteudoResposta)
                .map(body -> ClientResponse
                        .create(response.statusCode())
                        .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                        .body(body)
                        .build());

    }

    private Mono<String> validarConteudoResposta(String conteudo) {
        LOG.info("Resposta: {}", conteudo);
        if (conteudo.contains(ERRO_PADRAO)) {
            return Mono.empty();
        }
        return Mono.just(conteudo);
    }

}