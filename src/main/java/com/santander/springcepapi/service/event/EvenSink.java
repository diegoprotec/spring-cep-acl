package com.santander.springcepapi.service.event;

import com.santander.springcepapi.service.event.subscriber.CepEvent;
import com.santander.springcepapi.util.rest.response.EventResponse;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface EvenSink<T, R> {

    void emitirEvento(T evento);

    Flux<T> subscreverEventos();

    Flux<T> subscreverEventos(String id);

    ServerSentEvent<EventResponse<R>> mapearParaServerSentEvent(CepEvent evento);

}
