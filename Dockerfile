# Definir ARGs globais
ARG LINK=/linkimage
ARG JAVA_VERSION=22
ARG CLASS_PATH=$HOME/.gradle/caches/modules-2/files-2.1
ARG MODULES=modules.txt

# Build stage
FROM eclipse-temurin:22 AS builder
ARG LINK
ARG JAVA_VERSION
ARG CLASS_PATH
ARG MODULES
WORKDIR /app

COPY . .
RUN ./gradlew build -x test

# Cria classpath das dependências
RUN (find "$CLASS_PATH" -name "*.jar" -type f && \
    find build/libs -name "*.jar" ! -name "*-plain.jar") | \
    tr '\n' ':' > classpath.txt

# Analisa todas as dependências
RUN JARFILE=$(find build/libs/ -name '*.jar' ! -name '*-plain.jar') && \
    jdeps \
    --ignore-missing-deps \
    --multi-release $JAVA_VERSION \
    --recursive \
    --print-module-deps \
    --class-path @./classpath.txt \
    ${JARFILE} > modules.txt && \
    sed -i 's/$/,java.desktop,java.sql,java.naming,java.management,java.instrument,jdk.unsupported,java.net.http,java.net.http,java.security.jgss/' $MODULES

# Adiciona módulos base do java e cria runtime otimizado
RUN jlink --add-modules $(cat $MODULES) --strip-debug --no-header-files --no-man-pages --output $LINK

#########################################################################################################
#########################################################################################################
# Runtime stage
FROM debian:bookworm-slim
ARG LINK
WORKDIR /app

ENV TZ=America/Sao_Paulo
ENV JAVA_HOME=/opt/java
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV PATH="$JAVA_HOME/bin:${PATH}"

COPY --from=builder $LINK $JAVA_HOME
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar app.jar"]
