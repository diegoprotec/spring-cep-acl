package com.santander.springcepapi.config.webflux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    private static final String VIA_CEP_BASE_URL = "https://viacep.com.br/ws/";

    @Value("${api.viacep.timeout-seconds:5}")
    private int timeoutSeconds;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(VIA_CEP_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds));
    }

    @Bean
    public CepClient cepClient(WebClient webClient) {
        var webClientAdapter = WebClientAdapter.create(webClient);
        var proxyFactory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();
        return proxyFactory.createClient(CepClient.class);
    }
}