package com.santander.springcepapi.repository.config;

import com.santander.springcepapi.model.entity.Cep;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

public class MockDynamoDbClient implements DynamoDbClient {

    @Override
    public DescribeTableResponse describeTable(DescribeTableRequest request) {
        return DescribeTableResponse.builder()
                .table(TableDescription.builder()
                        .tableName(Cep.CEP_TABLE_NAME)
                        .tableStatus(TableStatus.ACTIVE)
                        .build())
                .build();
    }

    @Override
    public String serviceName() {
        return "DynamoDB";
    }

    @Override
    public void close() {
    }

}

