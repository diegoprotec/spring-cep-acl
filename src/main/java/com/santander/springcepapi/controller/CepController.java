package com.santander.springcepapi.controller;

import com.santander.springcepapi.controller.docs.CepControllerDoc;
import com.santander.springcepapi.exception.constraint.cep.CepConstraint;
import com.santander.springcepapi.model.vo.CepVo;
import com.santander.springcepapi.service.event.CepServiceEvent;
import com.santander.springcepapi.service.CepService;
import com.santander.springcepapi.util.rest.response.EventResponse;
import jakarta.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/cep")
public class CepController implements CepControllerDoc {

    private final CepService cepService;

    private final CepServiceEvent cepServiceEvent;

    @Autowired
    public CepController(CepService cepService, CepServiceEvent cepServiceEvent) {
        this.cepService = cepService;
        this.cepServiceEvent = cepServiceEvent;
    }

    @Override
    @GetMapping
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public List<CepVo> listaCeps() {
        return cepService.buscarTodos();
    }

    @GetMapping("/{cep}")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public Mono<CepVo> buscaCep(@PathVariable @CepConstraint String cep) {
        return cepService.buscar(cep);
    }

    @GetMapping(path = "/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<EventResponse<CepVo>>> streamEventos() {
        return cepServiceEvent.subscreverEventos()
                .map(cepServiceEvent::mapearParaServerSentEvent);
    }

    @GetMapping(path = "/{cep}/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<EventResponse<CepVo>>> streamEventosCep(@PathVariable @CepConstraint String cep) {
        return cepServiceEvent.subscreverEventos(cep)
                .map(cepServiceEvent::mapearParaServerSentEvent);
    }

}
