package com.santander.springcepapi.repository;

import com.santander.springcepapi.model.entity.Cep;
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
public class CepRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CepRepository.class);

    private final DynamoDbTable<Cep> cepTable;

    public CepRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        LOG.info("Inicialização da tabela");
        this.cepTable = dynamoDbEnhancedClient.table(Cep.CEP_TABLE_NAME, TableSchema.fromBean(Cep.class));
    }

    public List<Cep> findAll() {
        LOG.info("Buscar ceps");
        return cepTable.scan().items().stream().collect(Collectors.toList());
    }

    public void add(Cep cep) {
        LOG.info("Salvar cep: {}", cep.getCep());
        cepTable.putItem(cep);
    }

    public Cep get(String cep) {
        LOG.info("Buscar cep: {}", cep);
        Key partitionKey = Key.builder().partitionValue(cep).build();
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                .key(partitionKey)
                .consistentRead(true)
                .build();
        return cepTable.getItem(request);
    }

}
