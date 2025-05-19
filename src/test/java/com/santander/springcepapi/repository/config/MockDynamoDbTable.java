package com.santander.springcepapi.repository.config;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClientExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DescribeTableEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

public record MockDynamoDbTable<T>(String tableName) implements DynamoDbTable<T> {

    @Override
    public DynamoDbIndex<T> index(String indexName) {
        return null;
    }

    @Override
    public DescribeTableEnhancedResponse describeTable() {
        TableDescription tableDescription = TableDescription.builder()
                .tableName(tableName)
                .tableStatus(TableStatus.ACTIVE)
                .build();

        DescribeTableResponse response = DescribeTableResponse.builder()
                .table(tableDescription)
                .build();

        return DescribeTableEnhancedResponse.builder()
                .response(response)
                .build();
    }

    @Override
    public DynamoDbEnhancedClientExtension mapperExtension() {
        return null;
    }

    @Override
    public TableSchema<T> tableSchema() {
        return null;
    }

    @Override
    public String tableName() {
        return "";
    }

    @Override
    public Key keyFrom(T item) {
        return null;
    }

}


