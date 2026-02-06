# Music API - Guia de Deploy com Docker

## 📋 Visão Geral

Este guia explica como executar toda a infraestrutura da Music API usando Docker Compose, incluindo:
- PostgreSQL (Banco de dados)
- Redis (Cache)
- MinIO (Armazenamento de objetos)
- Music API (Aplicação Spring Boot)

## 🚀 Deploy Rápido

### Opção 1: Usando o Script de Deploy (Recomendado)

```bash
# Deploy completo (build JAR + build Docker + start)
./deploy.sh deploy
```

### Opção 2: Comandos Manuais

```bash
# 1. Gerar o JAR
./mvnw clean package -DskipTests
cp target/music-api-0.0.1-SNAPSHOT.jar music-app.jar

# 2. Iniciar todos os serviços
docker compose up -d

# 3. Acompanhar logs
docker compose logs -f music-api
```

## 📦 Comandos do Script de Deploy

O script `deploy.sh` fornece os seguintes comandos:

```bash
./deploy.sh build      # Gera o JAR da aplicação
./deploy.sh docker     # Constrói a imagem Docker
./deploy.sh start      # Inicia todos os serviços
./deploy.sh deploy     # Build completo + start
./deploy.sh logs       # Exibe logs da aplicação
./deploy.sh stop       # Para todos os serviços
./deploy.sh restart    # Reinicia todos os serviços
./deploy.sh clean      # Remove containers, volumes e imagens
./deploy.sh status     # Mostra status dos serviços
```

## 🌐 Acessos

Após inicialização bem-sucedida:

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| API | http://localhost:8080/api | - |
| Swagger UI | http://localhost:8080/api/swagger-ui.html | - |
| Login | http://localhost:8080/api/login | admin/admin123 |
| MinIO Console | http://localhost:9001 | admin/admin123 |
| PostgreSQL | localhost:5432 | postgres/admin |
| Redis | localhost:6379 | - |

## 🔧 Arquitetura Docker

### Serviços

#### 1. PostgreSQL
- **Imagem:** postgres:14
- **Porta:** 5432
- **Volume:** postgres_data (persistente)
- **Database:** padawan_api

#### 2. Redis
- **Imagem:** redis:latest
- **Porta:** 6379
- **Uso:** Cache de sessões e dados temporários

#### 3. MinIO
- **Imagem:** minio/minio:latest
- **Portas:** 9000 (API), 9001 (Console)
- **Volume:** minio_data (persistente)
- **Buckets:** meu-bucket, outro-bucket

#### 4. Music API
- **Imagem:** Construída localmente (openjdk:21-jdk-slim)
- **Porta:** 8080
- **Dependências:** Aguarda PostgreSQL, Redis e MinIO estarem saudáveis

### Rede

Todos os serviços estão conectados à rede `music-api-network` (bridge), permitindo comunicação interna usando nomes de serviço.

### Volumes Persistentes

- `postgres_data`: Dados do PostgreSQL
- `minio_data`: Arquivos do MinIO

## 🔍 Monitoramento e Logs

### Ver logs de todos os serviços
```bash
docker compose logs -f
```

### Ver logs de um serviço específico
```bash
docker compose logs -f music-api
docker compose logs -f postgres
docker compose logs -f redis
docker compose logs -f minio
```

### Verificar status dos containers
```bash
docker compose ps
./deploy.sh status
```

### Health Checks

Cada serviço possui health checks configurados:
- **PostgreSQL:** `pg_isready -U postgres`
- **Redis:** `redis-cli ping`
- **MinIO:** Endpoint `/minio/health/live`
- **Music API:** Endpoint `/api/actuator/health`

## 🛠️ Troubleshooting

### Problema: Serviços não iniciam

```bash
# Verificar logs
docker compose logs

# Verificar status
docker compose ps

# Reiniciar serviços
./deploy.sh restart
```

### Problema: Porta já em uso

Edite `docker-compose.yml` ou `.env` para alterar as portas:
```yaml
ports:
  - "8081:8080"  # Alterar porta externa
```

### Problema: Aplicação não conecta ao banco

```bash
# Verificar se PostgreSQL está saudável
docker compose ps postgres

# Verificar logs do PostgreSQL
docker compose logs postgres

# Acessar container da aplicação
docker compose exec music-api /bin/bash
```

### Problema: MinIO não cria buckets

```bash
# Verificar logs do minio-init
docker compose logs minio-init

# Recriar buckets manualmente
docker compose exec minio mc alias set local http://localhost:9000 admin admin123
docker compose exec minio mc mb local/meu-bucket
```

### Limpar e recomeçar

```bash
# Parar e remover tudo (incluindo volumes)
./deploy.sh clean

# Rebuild completo
./deploy.sh deploy
```

## 🔄 Atualizações

### Atualizar código da aplicação

```bash
# 1. Rebuild JAR
./deploy.sh build

# 2. Rebuild imagem Docker
./deploy.sh docker

# 3. Reiniciar apenas a aplicação
docker compose up -d --force-recreate music-api
```

### Atualizar dependências

```bash
# Editar pom.xml
# Rebuild completo
./deploy.sh clean
./deploy.sh deploy
```

## 📝 Personalização

### Variáveis de Ambiente

Copie `.env.example` para `.env` e ajuste conforme necessário:

```bash
cp .env.example .env
```

Edite `.env`:
```env
POSTGRES_PASSWORD=sua_senha_segura
MINIO_ROOT_PASSWORD=outra_senha_segura
API_PORT=8080
```

### Configurações da Aplicação

As configurações são injetadas via variáveis de ambiente no `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/padawan_api
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: admin
  # ... outras configurações
```

## 🚀 Deploy em Produção

### Considerações

1. **Segurança:**
   - Altere todas as senhas padrão
   - Use secrets do Docker para credenciais
   - Configure HTTPS/SSL
   - Limite exposição de portas

2. **Performance:**
   - Ajuste recursos (CPU/Memória) no docker-compose
   - Configure pool de conexões do PostgreSQL
   - Aumente timeout dos health checks se necessário

3. **Backup:**
   - Configure backup automático dos volumes
   - Exporte dados do PostgreSQL regularmente
   - Backup do MinIO

4. **Monitoramento:**
   - Integre com ferramentas de APM
   - Configure alertas para falhas
   - Use centralizador de logs

### Exemplo de Configuração com Recursos Limitados

```yaml
music-api:
  # ... outras configurações
  deploy:
    resources:
      limits:
        cpus: '1.0'
        memory: 1G
      reservations:
        cpus: '0.5'
        memory: 512M
```

## 📚 Referências

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
- [MinIO Docker Guide](https://min.io/docs/minio/container/index.html)
