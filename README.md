# Cep ACL

## Descrição
API para consulta de CEP

## Tecnologias Utilizadas
- Java 22
- Spring Boot 3.4.5
- AWS DynamoDB
- Docker
- Gradle

## Pré-requisitos
- Docker instalado
- Java 22 instalado
- Gradle instalado
- AWS CLI (opcional, para interagir com DynamoDB local)

# Execução
## Docker Compose
1. Construir e iniciar todos os serviços
   ```
   docker-compose up --build
   ```
2. Executar em background
   ```
   docker-compose up -d
   ```
3. Parar todos os serviços
   ```
   docker-compose down
   ```
4. Ver logs
   ```
   docker-compose logs -f
   ```
5. Ver logs de um serviço específico
   ```
   docker-compose logs -f app
   ```

## Manualmente
1. DynamoDB:
    ```
    docker pull amazon/dynamodb-local
    docker run -p 8000:8000 amazon/dynamodb-local
    ```
2. Verifique se o DynamoDB local está rodando:
    ```
    curl http://localhost:4566/shell
    ```
3. Aplicação:\
   Docker
   ```
   docker build -t minha-app:latest .
   docker run -p 8081:8080 minha-app:latest
   ```
   Gradle
   ```
   ./gradlew build
   ./gradlew bootRun
   ```

## AWS CLI - Verificando o DynamoDB Local
Para verificar se as tabelas foram criadas corretamente, você pode usar o AWS CLI:
```
aws dynamodb list-tables --endpoint-url http://localhost:4566
```

