package com.santander.springcepapi.service;

import com.santander.springcepapi.client.CepClient;
import com.santander.springcepapi.exception.constraint.cep.CepConstraint;
import com.santander.springcepapi.model.CepMapper;
import com.santander.springcepapi.model.entity.Cep;
import com.santander.springcepapi.model.vo.CepVo;
import com.santander.springcepapi.repository.CepRepository;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class CepService {

    private static final Logger LOG = LoggerFactory.getLogger(CepService.class);
    public static final String BUSCANDO_NO_BANCO = "Buscando CEP {} no banco de dados";
    public static final String BUSCANDO_NA_API = "Buscando CEP {} na API externa";
    public static final String ENCONTRADO_COM_SUCESSO = "CEP {} encontrado com sucesso";
    public static final String NAO_ENCONTRADO = "CEP %s não encontrado";

    private final CepRepository cepRepository;
    private final CepClient cepClient;

    @Autowired
    public CepService(CepRepository cepRepository, CepClient cepClient) {
        this.cepRepository = cepRepository;
        this.cepClient = cepClient;
    }

    public List<CepVo> buscarTodos() {
        List<Cep> lista = cepRepository.findAll();
        if (lista.isEmpty()) {
            return List.of();
        }

        return lista.stream()
                .map(CepMapper.INSTANCE::toVo)
                .collect(Collectors.toList());
    }

    public Mono<CepVo> buscar(@CepConstraint String cep) {
        return Mono.justOrEmpty(cepRepository.get(cep))
                .doOnSubscribe(s -> LOG.info(BUSCANDO_NO_BANCO, cep))
                .map(CepMapper.INSTANCE::toVo)
                .switchIfEmpty(
                        cepClient.buscaCep(cep)
                                .doOnSubscribe(s -> LOG.info(BUSCANDO_NA_API, cep))
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                        .onRetryExhaustedThrow((s1, s2) ->
                                                new NotFoundException(String.format(NAO_ENCONTRADO, cep)))
                                )
                                .doOnNext(this::salvar)
                )
                .doOnNext(result -> LOG.info(ENCONTRADO_COM_SUCESSO, result.cep()));
    }

    @Async
    protected void salvar(CepVo cepVo) {
        cepRepository.add(CepMapper.INSTANCE.toDocument(cepVo));
    }

}
