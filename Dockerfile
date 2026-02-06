# Usar imagem base do Java 21
FROM openjdk:21-jdk-slim

# Criar diretório de trabalho
WORKDIR /app

# Copiar o JAR da aplicação
COPY music-app.jar app.jar

# Expor porta 8080
EXPOSE 8080

# Variáveis de ambiente padrão (podem ser sobrescritas no docker-compose)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/padawan_api
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=admin
ENV SPRING_REDIS_HOST=redis
ENV SPRING_REDIS_PORT=6379
ENV MINIO_URL=http://minio:9000
ENV MINIO_ACCESS_KEY=admin
ENV MINIO_SECRET_KEY=admin123

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
