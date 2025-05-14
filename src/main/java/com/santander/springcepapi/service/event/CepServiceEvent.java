package com.santander.springcepapi.service.event;

import com.santander.springcepapi.model.vo.CepVo;
import com.santander.springcepapi.service.event.subscriber.CepEvent;
import com.santander.springcepapi.service.event.subscriber.CepSubscriberManager;
import com.santander.springcepapi.util.rest.response.EventResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Component
public class CepServiceEvent implements EvenSink<CepEvent, CepVo> {

    private final CepSubscriberManager subscriberManager;
    private final Sinks.Many<CepEvent> cepEventSink;

    private static final String EVENTO_CEP_BUSCADO = "cep-buscado";
    private static final String EVENTO_CEP_SALVO = "cep-salvo";
    private static final String EVENTO_CEP_ERRO = "cep-erro";

    @Autowired
    public CepServiceEvent(CepSubscriberManager subscriberManager) {
        this.subscriberManager = subscriberManager;
        this.cepEventSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @Override
    public void emitirEvento(CepEvent evento) {
        Sinks.EmitResult result = cepEventSink.tryEmitNext(evento);
        if (result.isFailure()) {
            throw new IllegalStateException("Falha ao emitir evento: " + result);
        }
    }

    @Override
    public Flux<CepEvent> subscreverEventos() {
        return cepEventSink.asFlux()
                .doOnSubscribe(subscriberManager::registrarSubscriberEventos);
    }

    @Override
    public Flux<CepEvent> subscreverEventos(String id) {
        return cepEventSink.asFlux()
                .filter(evento -> evento.getCep().equals(id))
                .doOnSubscribe(sub -> subscriberManager.registrarSubscriberEventosCep(id, sub));
    }

    @Override
    public ServerSentEvent<EventResponse<CepVo>> mapearParaServerSentEvent(CepEvent evento) {
        return switch (evento) {
            case CepEvent.CepBuscadoEvent e -> criarEvento(EVENTO_CEP_BUSCADO, EventResponse.ofSuccess(e.dados()));
            case CepEvent.CepSalvoEvent e -> criarEvento(EVENTO_CEP_SALVO, EventResponse.ofSuccess(e.dados()));
            case CepEvent.CepErroEvent e -> criarEvento(EVENTO_CEP_ERRO, EventResponse.ofError(e.mensagem()));
        };
    }

    private <T> ServerSentEvent<T> criarEvento(String tipo, T dado) {
        return ServerSentEvent.<T>builder()
                .id(UUID.randomUUID().toString())
                .event(tipo)
                .data(dado)
                .build();
    }

}
