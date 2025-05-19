package com.santander.springcepapi.repository.config;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class MockDynamoDbEnhancedClient implements DynamoDbEnhancedClient {

    @Override
    public <T> DynamoDbTable<T> table(String tableName, TableSchema<T> tableSchema) {
        return new MockDynamoDbTable<>(tableName);
    }

}
