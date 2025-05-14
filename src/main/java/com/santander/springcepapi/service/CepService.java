package com.santander.springcepapi.service;

import com.santander.springcepapi.client.CepClient;
import com.santander.springcepapi.exception.constraint.cep.CepConstraint;
import com.santander.springcepapi.model.CepMapper;
import com.santander.springcepapi.model.entity.Cep;
import com.santander.springcepapi.model.vo.CepVo;
import com.santander.springcepapi.repository.CepRepository;
import com.santander.springcepapi.service.event.subscriber.CepEvent;
import com.santander.springcepapi.service.event.EvenSink;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Validated
public class CepService {

    private static final Logger LOG = LoggerFactory.getLogger(CepService.class);
    private final CepRepository cepRepository;
    private final CepClient cepClient;
    private final EvenSink<CepEvent, CepVo> evenSink;

    public static final String BUSCANDO_NO_BANCO = "Buscando CEP {} no banco de dados";
    public static final String BUSCANDO_NA_API = "Buscando CEP {} na API externa";
    public static final String NAO_ENCONTRADO = "CEP %s não encontrado";

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(1);

    @Autowired
    public CepService(CepRepository cepRepository, CepClient cepClient, EvenSink<CepEvent, CepVo> evenSink) {
        this.cepRepository = cepRepository;
        this.cepClient = cepClient;
        this.evenSink = evenSink;
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
        return buscarNoBanco(cep)
                .switchIfEmpty(buscarNaApi(cep));
    }

    private Mono<CepVo> buscarNoBanco(String cep) {
        return Mono.justOrEmpty(cepRepository.get(cep))
                .map(CepMapper.INSTANCE::toVo)
                .doOnNext(cepVo ->
                        this.evenSink.emitirEvento(new CepEvent.CepBuscadoEvent(cep, cepVo, LocalDateTime.now())))
                .doOnSubscribe(_ -> LOG.info(BUSCANDO_NO_BANCO, cep));
    }

    private Mono<CepVo> buscarNaApi(String cep) {
        return cepClient.buscaCep(cep)
                .doOnSubscribe(_ -> LOG.info(BUSCANDO_NA_API, cep))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .onRetryExhaustedThrow((_, _) -> {
                            var erro = new NotFoundException(String.format(NAO_ENCONTRADO, cep));
                            this.evenSink.emitirEvento(new CepEvent.CepErroEvent(cep, erro.getMessage(), LocalDateTime.now()));
                            return erro;
                        }))
                .doOnNext(this::persistirCepAssincronamente);
    }

    @Async("asyncExecutor")
    public void persistirCepAssincronamente(CepVo cepVo) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executarPersistencia(cepVo)
                .doOnSuccess(cepSalvo -> {
                    notificarSucesso(cepSalvo);
                    future.complete(null);
                })
                .doOnError(erro -> {
                    notificarError(cepVo, erro);
                    future.completeExceptionally(erro);
                })
                .subscribe();
    }

    private Mono<CepVo> executarPersistencia(CepVo cepVo) {
        return cepRepository.add(CepMapper.INSTANCE.toDocument(cepVo))
                .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, RETRY_DELAY))
                .map(CepMapper.INSTANCE::toVo);
    }

    private void notificarSucesso(CepVo cepVo) {
        LOG.error("CEP '{}' salvo com sucesso", cepVo.cep());
        this.evenSink.emitirEvento(new CepEvent.CepSalvoEvent(cepVo.cep(), cepVo, LocalDateTime.now()));
    }

    private void notificarError(CepVo cepVo, Throwable e) {
        LOG.error("Falha ao salvar CEP após tentativas", e);
        this.evenSink.emitirEvento(new CepEvent.CepErroEvent(cepVo.cep(), e.getMessage(), LocalDateTime.now()));
    }


}
