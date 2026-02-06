# Dockerfile para app-music.jar
FROM eclipse-temurin:21-jre-alpine

# Diretório de trabalho
WORKDIR /app

# Copiar JAR pré-compilado
COPY app-music.jar app.jar

# Expor porta
EXPOSE 8080

# Variáveis de ambiente
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/padawan_api
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=admin
ENV SPRING_REDIS_HOST=redis
ENV SPRING_REDIS_PORT=6379
ENV MINIO_URL=http://minio:9000
ENV MINIO_ACCESS_KEY=admin
ENV MINIO_SECRET_KEY=admin123

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
