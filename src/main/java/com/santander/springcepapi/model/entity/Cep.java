package com.santander.springcepapi.model.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;

@DynamoDbBean
public class Cep {

    public static final String CEP_TABLE_NAME = "cep";
    public static final String CEP_KEY = "cep";
    public static final String CEP_LOGRADOURO_COL = "logradouro";
    public static final String CEP_BAIRRO_COL = "bairro";
    public static final String CEP_LOCALIDADE_COL = "localidade";
    public static final String CEP_ESTADO_COL = "estado";

    public Cep() {
    }

    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String estado;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(CEP_KEY)
    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    @DynamoDbAttribute(CEP_LOGRADOURO_COL)
    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    @DynamoDbAttribute(CEP_BAIRRO_COL)
    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    @DynamoDbAttribute(CEP_LOCALIDADE_COL)
    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    @DynamoDbAttribute(CEP_ESTADO_COL)
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Cep cepObj)) {
            return false;
        }
        return Objects.equals(getCep(), cepObj.getCep());
    }

    @Override
    public int hashCode() {
        return Objects.hash(cep, logradouro, bairro, localidade, estado);
    }

    @Override
    public String toString() {
        return "Cep{"
                + String.format("cep='%s', ", cep)
                + String.format("logradouro='%s', ", logradouro)
                + String.format("bairro='%s', ", bairro)
                + String.format("localidade='%s', ", localidade)
                + String.format("estado='%s'", estado)
                + '}';
    }


}

