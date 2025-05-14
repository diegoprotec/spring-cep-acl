package com.santander.springcepapi.service.event.subscriber;

import com.santander.springcepapi.model.vo.CepVo;

import java.time.LocalDateTime;

public sealed interface CepEvent {
    String getCep();

    record CepBuscadoEvent(String cep, CepVo dados, LocalDateTime timestamp) implements CepEvent {
        @Override
        public String getCep() {
            return cep;
        }
    }

    record CepSalvoEvent(String cep, CepVo dados, LocalDateTime timestamp) implements CepEvent {
        @Override
        public String getCep() {
            return cep;
        }
    }

    record CepErroEvent(String cep, String mensagem, LocalDateTime timestamp) implements CepEvent {
        @Override
        public String getCep() {
            return cep;
        }
    }

}
