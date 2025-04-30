package com.santander.springcepapi;

import com.santander.springcepapi.boundary.cep.domain.Cep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.DescribeTableEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import static org.mockito.Mockito.when;

@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
public class SpringCepAclApplicationTest {

    @Mock
    DynamoDbTable<Cep> mockTable;

    @BeforeEach
    void setup() {
        when(mockTable.describeTable()).thenReturn(DescribeTableEnhancedResponse.builder()
                .response(DescribeTableResponse.builder()
                        .table(TableDescription.builder()
                                .tableName(Cep.CEP_TABLE_NAME)
                                .tableStatus(TableStatus.ACTIVE)
                                .build())
                        .build())
                .build());
    }

    @Test
    void contextLoads() {
    }

}
