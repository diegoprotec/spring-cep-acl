package com.santander.springcepapi;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.mockito.Mockito.doNothing;

@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
@ActiveProfiles("test")
public class SpringCepAclApplicationTest {

    @MockitoBean
    private DynamoDbClient dynamoDbClient;

    @MockitoBean
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @MockitoBean
    private CommandLineRunner commandLineRunner;

    @Test
    void contextLoads() {
        try {
            doNothing().when(commandLineRunner).run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
