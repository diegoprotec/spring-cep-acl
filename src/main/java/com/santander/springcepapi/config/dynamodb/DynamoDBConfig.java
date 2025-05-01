package com.santander.springcepapi.config.dynamodb;

import com.santander.springcepapi.boundary.cep.domain.Cep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDBConfig.class);

    @Configuration
    @Profile("prod")
    public static class DynamoDBRemoteConfig {
        @Value("${aws.region}")
        private String region;

        @Bean
        public DynamoDbClient dynamoDbClient() {
            LOG.info("REMOTO: Configurando DynamoDbClient");
            return DynamoDbClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

    @Configuration
    @Profile("!prod")
    public static class LocalDynamoDBConfig {
        @Value("${aws.region:sa-east-1}")
        private String region;

        @Value("${aws.endpoint:uri}")
        private String endpoint;

        @Value("${aws.accessKeyId:test}")
        private String accessKeyId;

        @Value("${aws.secretKey:test}")
        private String secretKey;

        @Bean
        public DynamoDbClient dynamoDbClient() {
            LOG.info("LOCAL: Configurando DynamoDbClient url: {}", endpoint);
            return DynamoDbClient.builder()
                    .endpointOverride(URI.create(endpoint))
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKeyId, secretKey)
                    ))
                    .build();
        }

    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public CommandLineRunner initializeDynamoDB(DynamoDbEnhancedClient enhancedClient) {
        var tableName = Cep.CEP_TABLE_NAME;
        return args -> {
            try {
                DynamoDbTable<Cep> cepTable = enhancedClient.table(tableName,
                        TableSchema.fromBean(Cep.class));

                LOG.info("Verificando existência da tabela {}", tableName);
                try {
                    var describeTableResponse = cepTable.describeTable();
                    var tableDescription = describeTableResponse.table();
                    LOG.info("Tabela {} já existe. Status: {}", tableName, tableDescription.tableStatus());
                } catch (ResourceNotFoundException e) {
                    LOG.info("Tabela não encontrada");
                    cepTable.createTable(builder -> builder
                            .provisionedThroughput(b -> b
                                    .readCapacityUnits(5L)
                                    .writeCapacityUnits(5L)
                                    .build()));
                    LOG.info("Tabela criada com sucesso!");
                    LOG.info("Status: {}", cepTable.describeTable().table().tableStatus());
                }
            } catch (Exception e) {
                LOG.error("Erro na inicialização do DynamoDB: {}", e.getMessage());
                throw new RuntimeException("Falha na inicialização do DynamoDB", e.getCause());
            }
        };

    }


}

