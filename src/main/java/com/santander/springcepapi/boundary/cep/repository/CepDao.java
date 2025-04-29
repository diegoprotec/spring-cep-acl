package com.santander.springcepapi.boundary.cep.repository;

import com.santander.springcepapi.boundary.cep.domain.Cep;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CepDao implements CepRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CepDao.class);
    private static final String ERRO_CEP_NAO_ENCONTRADO = "Não foi encontrado na base de dados o CEP: %s";

    private final DynamoDbTable<Cep> cepTable;

    public CepDao(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        LOG.info("Conexão com a base de dados");
        this.cepTable = dynamoDbEnhancedClient.table(Cep.CEP_TABLE_NAME, TableSchema.fromBean(Cep.class));
    }

    @Override
    public List<Cep> findAll() {
        LOG.info("Buscando todos os registros...");
        return cepTable.scan().items().stream().collect(Collectors.toList());
    }

    @Override
    public void add(Cep cep) {
        LOG.debug("Cep class loader: {}", Cep.class.getClassLoader());
        LOG.debug("TableSchema class loader: {}", TableSchema.class.getClassLoader());
        LOG.info("Salvando o cep: {}...", cep.getCep());
        cepTable.putItem(cep);
    }

    @Override
    public Cep get(String cep) {
        LOG.info("Buscando o cep: {}...", cep);

        Key partitionKey = Key.builder().partitionValue(cep).build();
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                .key(partitionKey)
                .consistentRead(true)
                .build();
        Cep cepDocumento = cepTable.getItem(request);
        if (cepDocumento == null) {
            throw new NotFoundException(String.format(ERRO_CEP_NAO_ENCONTRADO, cep));
        }

        return cepDocumento;
    }

}
