package com.santander.springcepapi.boundary.cep.rest;

import com.santander.springcepapi.boundary.cep.application.CepApplication;
import com.santander.springcepapi.boundary.cep.domain.CepVo;
import com.santander.springcepapi.boundary.cep.rest.doc.CepResourceDoc;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cep")
public class CepResource implements CepResourceDoc {

    private final CepApplication cepApplication;

    @Autowired
    public CepResource(CepApplication cepApplication) {
        this.cepApplication = cepApplication;
    }

    @Override
    @GetMapping("/{cep}")
    @Produces(MediaType.APPLICATION_JSON)
    public CepVo buscaCep(@PathVariable String cep) {
        return cepApplication.buscar(cep);
    }

    @Override
    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    public List<CepVo> listarCeps() {
        return cepApplication.buscarTodos();
    }

}
