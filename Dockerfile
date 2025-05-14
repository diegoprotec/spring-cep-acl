FROM eclipse-temurin:22-jdk-jammy as builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
RUN jlink --add-modules $(jdeps --print-module-deps build/libs/*.jar) \
    --strip-debug --compress 2 --no-header-files --no-man-pages \
    --output /javaruntime

# Runtime stage
FROM debian:slim-bookworm
WORKDIR /app

COPY --from=builder /javaruntime /opt/java
COPY --from=builder /app/build/libs/*.jar app.jar

# Definindo variáveis de ambiente com valores default
# Adicionar estas flags ao JAVA_OPTS para melhor performance em containers
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV TZ=America/Sao_Paulo
ENV PATH="${PATH}:/opt/java/bin"

ENV SPRING_PROFILES_ACTIVE=dev
ENV SERVER_PORT=8080

EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["$JAVA_OPTS", "-jar", "app.jar"]
