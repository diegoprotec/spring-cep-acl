package com.santander.springcepapi.boundary.cep.repository;

import com.santander.springcepapi.boundary.cep.domain.Cep;

import java.util.List;

public interface CepRepository {

    List<Cep> findAll();

    void add(Cep cep);

    Cep get(String cep);
}
