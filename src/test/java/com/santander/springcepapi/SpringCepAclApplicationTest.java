package com.santander.springcepapi;

import com.santander.springcepapi.boundary.cep.domain.Cep;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DescribeTableEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
public class SpringCepAclApplicationTest {

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbTable<Cep> mockTable;

    @Test
    void contextLoads() {
        TableSchema<Cep> tableSchema = TableSchema.fromBean(Cep.class);
        doReturn(mockTable).when(dynamoDbEnhancedClient).table(Cep.CEP_TABLE_NAME, tableSchema);
        when(mockTable.describeTable()).thenReturn(DescribeTableEnhancedResponse.builder()
                .response(DescribeTableResponse.builder()
                        .table(TableDescription.builder()
                                .tableName(Cep.CEP_TABLE_NAME)
                                .tableStatus(TableStatus.ACTIVE)
                                .build())
                        .build())
                .build());
    }

}
