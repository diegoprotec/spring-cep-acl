package com.santander.springcepapi.service;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.santander.springcepapi.client.CepClient;
import com.santander.springcepapi.model.entity.Cep;
import com.santander.springcepapi.model.CepMapper;
import com.santander.springcepapi.model.vo.CepVo;
import com.santander.springcepapi.exception.constraint.cep.CepConstraint;
import com.santander.springcepapi.repository.CepRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class CepService {

    private static final Logger LOG = LoggerFactory.getLogger(CepService.class);
    private static final String ERRO_CEP_NAO_ENCONTRADO = "CEP não encontrado: ";
    private static final String MENSAGEM_CONEXAO_VIACEP = "Conexão com a api ViaCep";

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

    public void salvar(CepVo cep) {
        cepRepository.add(CepMapper.INSTANCE.toDocument(cep));
    }

    public CepVo buscar(
            @NotBlank(message = "O campo 'cep' é obrigatório.")
            @Size(min = 8, max = 8, message = "O CEP deve ter exatamente 8 caracteres.")
            @CepConstraint
            String cep) {

        return cepRepository.get(cep)
                .map(CepMapper.INSTANCE::toVo)
                .orElseGet(() -> buscarViaApi(cep));
    }

    private CepVo buscarViaApi(String cep) {
        LOG.info(MENSAGEM_CONEXAO_VIACEP);
        try {
            CepVo cepExterno = cepClient.buscaCep(cep);
            salvarAsync(cepExterno);
            return cepExterno;
        } catch (WebClientResponseException.NotFound e) {
            throw new NotFoundException(ERRO_CEP_NAO_ENCONTRADO + cep);
        } catch (Exception ex) {
            if (ex.getCause() instanceof ValueInstantiationException) {
                throw new NotFoundException(ERRO_CEP_NAO_ENCONTRADO + cep);
            }
            throw ex;
        }
    }

    @Async
    protected void salvarAsync(CepVo cep) {
        salvar(cep);
    }

}
