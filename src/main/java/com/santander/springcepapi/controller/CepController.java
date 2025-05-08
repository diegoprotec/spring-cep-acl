package com.santander.springcepapi.controller;

import com.santander.springcepapi.controller.docs.CepControllerDoc;
import com.santander.springcepapi.exception.constraint.cep.CepConstraint;
import com.santander.springcepapi.model.vo.CepVo;
import com.santander.springcepapi.service.CepService;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/cep")
public class CepController implements CepControllerDoc {

    private final CepService cepService;

    @Autowired
    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @Override
    @GetMapping("/{cep}")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<CepVo> buscaCep(@PathVariable @CepConstraint String cep) {
        return cepService.buscar(cep);
    }

    @Override
    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    public List<CepVo> listaCeps() {
        return cepService.buscarTodos();
    }

}
