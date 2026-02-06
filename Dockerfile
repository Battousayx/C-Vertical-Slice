# ================================
# Music API - Production Dockerfile
# ================================
# Single-stage build com Ubuntu Linux
# Instala todas as dependências dentro da imagem Ubuntu

# ================================
# Base: Ubuntu 22.04 LTS
# ================================
FROM ubuntu:22.04

# Metadados da aplicação
LABEL maintainer="Music API Team"
LABEL version="1.0.0"
LABEL description="Music API - REST API para gerenciamento de artistas e álbuns"

# Evitar prompts interativos durante instalação
ENV DEBIAN_FRONTEND=noninteractive

# Instalar Java JDK 21, Maven e utilitários
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    openjdk-21-jdk-headless \
    maven \
    curl \
    ca-certificates \
    tzdata \
    git \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Definir JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Verificar instalações
RUN java -version && mvn -version

# Definir timezone
ENV TZ=America/Sao_Paulo

# Criar usuário não-root para execução
RUN groupadd -g 1001 musicapi && \
    useradd -u 1001 -g musicapi -s /bin/bash -m musicapi

# Definir diretório de trabalho para build
WORKDIR /build

# Copiar arquivos do projeto
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

# Build da aplicação como root (para ter permissões)
RUN mvn clean package -DskipTests -B && \
    mkdir -p /app && \
    mv target/music-api-*.jar /app/app.jar && \
    rm -rf /build /root/.m2

# Criar diretórios necessários
RUN mkdir -p /app/logs /app/temp && \
    chown -R musicapi:musicapi /app

# Definir diretório de trabalho
WORKDIR /app

# ================================
# Variáveis de Ambiente (Defaults)
# ================================
# Database
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/padawan_api
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=changeme

# Redis
ENV SPRING_REDIS_HOST=redis
ENV SPRING_REDIS_PORT=6379

# MinIO
ENV MINIO_URL=http://minio:9000
ENV MINIO_ACCESS_KEY=admin
ENV MINIO_SECRET_KEY=changeme
ENV MINIO_BUCKET_NAME=meu-bucket

# JWT (IMPORTANTE: alterar em produção)
ENV JWT_SECRET=changeme-use-a-strong-secret-key-here
ENV JWT_EXPIRATION=300000
ENV JWT_REFRESH_EXPIRATION=604800000

# Server
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# JVM Settings (otimizado para container)
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom \
    -Duser.timezone=America/Sao_Paulo"

# Logging
ENV LOGGING_LEVEL_ROOT=INFO
ENV LOGGING_LEVEL_BR_COM_MUSIC_API=INFO

# Multipart (upload de arquivos)
ENV SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
ENV SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB

# ================================
# Segurança e Permissões
# ================================
# Segurança e Permissões
# ================================
# Mudar para usuário não-root
USER musicapi

# Expor porta da aplicação
EXPOSE 8080

# ================================
# Health Check
# ================================
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/v1/auth/health || exit 1

# ================================
# Entrypoint
# ================================
# Usar shell form para permitir substituição de variáveis
ENTRYPOINT java $JAVA_OPTS -jar app.jar

# ================================
# Build & Run Instructions
# ================================
# Build:
#   docker build -t music-api:latest -t music-api:1.0.0 .
#
# Run (development):
#   docker run -p 8080:8080 --name music-api \
#     -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/padawan_api \
#     music-api:latest
#
# Run (production com .env):
#   docker run -p 8080:8080 --name music-api --env-file .env music-api:latest
#
# Shell access (debug):
#   docker exec -it music-api bash
#
# Logs:
#   docker logs -f music-api
