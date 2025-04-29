package com.santander.springcepapi.boundary.cep.application;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.santander.springcepapi.boundary.cep.domain.Cep;
import com.santander.springcepapi.boundary.cep.domain.CepMapper;
import com.santander.springcepapi.boundary.cep.domain.CepVo;
import com.santander.springcepapi.boundary.cep.repository.CepRepository;
import com.santander.springcepapi.config.webflux.CepClient;
import com.santander.springcepapi.exception.constraint.ConstraintException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CepApp implements CepApplication {

    private static final Logger LOG = LoggerFactory.getLogger(CepApp.class);
    private static final String ERRO_CEP_NAO_ENCONTRADO = "CEP não encontrado: ";
    private static final String MENSAGEM_CONEXAO_VIACEP = "Conexão com a api ViaCep";
    private static final String ERRO_VIACEP = "Falha na comunicação com a API ViaCep";

    private final Validator validator;
    private final CepRepository cepRepository;
    private final CepClient cepClient;


    @Autowired
    public CepApp(CepRepository cepRepository, Validator validator, CepClient cepClient) {
        this.cepRepository = cepRepository;
        this.validator = validator;
        this.cepClient = cepClient;
    }

    @Override
    public List<CepVo> buscarTodos() {
        List<Cep> lista = cepRepository.findAll();
        if (lista.isEmpty()) return List.of();

        return lista.stream()
                .map(CepMapper.INSTANCE::toVo)
                .collect(Collectors.toList());
    }

    @Override
    public void salvar(CepVo cep) {
        cepRepository.add(CepMapper.INSTANCE.toDocument(cep));
    }

    @Override
    public CepVo buscar(
            @NotBlank(message = "O CEP é obrigatório")
            @Pattern(regexp = "\\d{8}", message = "CEP deve ter exatamente 8 dígitos")
            String cep) {

        Set<ConstraintViolation<String>> violations = validator.validate(cep);
        if (!violations.isEmpty()) throw new ConstraintException(violations);

        try {
            Cep cepDocument = cepRepository.get(cep);
            return CepMapper.INSTANCE.toVo(cepDocument);
        } catch (NotFoundException e) {
            CepVo cepVoFinal = viaCepBuscar(cep);
            salvarAsync(cepVoFinal);
            return cepVoFinal;
        }
    }

    private CepVo viaCepBuscar(String cep) {
        LOG.info(MENSAGEM_CONEXAO_VIACEP);
        try {
            return cepClient.buscaCep(cep);
        } catch (WebClientResponseException.NotFound e) {
            throw new NotFoundException(ERRO_CEP_NAO_ENCONTRADO + cep);
        } catch (WebClientResponseException e) {
            throw new InternalServerErrorException(ERRO_VIACEP);
        } catch (Exception ex) {
            if (ex.getCause() instanceof ValueInstantiationException)
                throw new NotFoundException(ERRO_CEP_NAO_ENCONTRADO + cep);
            throw new InternalServerErrorException(ex.getMessage());
        }
    }

    @Async
    protected void salvarAsync(CepVo cep) {
        salvar(cep);
    }

}
