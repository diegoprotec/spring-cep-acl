package com.santander.springcepapi;

import com.santander.springcepapi.model.entity.Cep;
import com.santander.springcepapi.repository.config.MockDynamoDbClient;
import com.santander.springcepapi.repository.config.MockDynamoDbEnhancedClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SpringCepAclApplicationTest.TestConfig.class)
public class SpringCepAclApplicationTest {

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public DynamoDbClient dynamoDbClient() {
            return new MockDynamoDbClient();
        }

        @Bean
        @Primary
        public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
            return new MockDynamoDbEnhancedClient();
        }
    }

    private static final String TABLE_NAME = "MyTable";

    @Mock
    private DynamoDbClient dynamoDbClient;

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbTable<Cep> cepTable;

    private AutoCloseable closeable;

    @BeforeEach
    void configurarAmbienteDeTeste() {
        closeable = MockitoAnnotations.openMocks(this);
        when(dynamoDbEnhancedClient.table(eq(Cep.CEP_TABLE_NAME), eq(TableSchema.fromBean(Cep.class))))
                .thenReturn(cepTable);
    }

    @AfterEach
    void fecharMocks() throws Exception {
        closeable.close();
    }

    @Test
    void contextLoads() {
        DescribeTableResponse response = DescribeTableResponse.builder()
                .table(TableDescription.builder()
                        .tableName(TABLE_NAME)
                        .build())
                .build();

        when(dynamoDbClient.describeTable(any(DescribeTableRequest.class))).thenReturn(response);
    }

}
