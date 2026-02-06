# Music API - Guia de Deploy em Produção

## 📋 Índice
1. [Análise Completa do Projeto](#análise-completa-do-projeto)
2. [Dependências de Produção](#dependências-de-produção)
3. [Dockerfile Multi-Stage](#dockerfile-multi-stage)
4. [Deploy em Produção](#deploy-em-produção)
5. [Segurança](#segurança)
6. [Monitoramento](#monitoramento)
7. [Troubleshooting](#troubleshooting)

---

## 📊 Análise Completa do Projeto

### Tecnologias

| Categoria | Tecnologia | Versão | Propósito |
|-----------|-----------|--------|-----------|
| **Runtime** | Java | 21 | Execução da aplicação |
| **Framework** | Spring Boot | 3.5.10 | Framework principal |
| **Database** | PostgreSQL | 14+ | Banco de dados relacional |
| **Cache** | Redis | 7+ | Cache de sessões e dados |
| **Storage** | MinIO | Latest | Armazenamento de objetos (imagens) |
| **Migrations** | Liquibase | 4.31.1 | Controle de versão do banco |
| **Auth** | JWT (JJWT) | 0.12.3 | Autenticação e autorização |
| **API Docs** | SpringDoc OpenAPI | 2.0.2 | Documentação Swagger |
| **Template** | Thymeleaf | Latest | Páginas HTML (login) |

### Dependências Spring Boot

```xml
✓ spring-boot-starter-web          # REST API
✓ spring-boot-starter-data-jpa     # Persistência
✓ spring-boot-starter-security     # Segurança
✓ spring-boot-starter-validation   # Validação de dados
✓ spring-boot-starter-oauth2-authorization-server  # OAuth2
✓ spring-boot-starter-websocket    # WebSocket (tempo real)
✓ spring-boot-starter-thymeleaf    # Templates HTML
```

### Serviços Externos Requeridos

#### PostgreSQL
- **Porta:** 5432
- **Database:** padawan_api
- **Usuário:** postgres
- **Função:** Armazena artistas, álbuns, usuários, relacionamentos

#### Redis
- **Porta:** 6379
- **Função:** Cache de sessões, tokens JWT, dados temporários

#### MinIO
- **Portas:** 9000 (API), 9001 (Console)
- **Buckets:** meu-bucket, outro-bucket
- **Função:** Armazenamento de imagens de álbuns

### Recursos da Aplicação

- ✅ API RESTful completa (CRUD de artistas, álbuns, imagens)
- ✅ Autenticação JWT com refresh tokens
- ✅ Upload/download de imagens (MinIO)
- ✅ Documentação Swagger automática
- ✅ Validação de dados (Bean Validation)
- ✅ Migrações de banco (Liquibase)
- ✅ Página de login responsiva
- ✅ Segurança com Spring Security
- ✅ Suporte a WebSocket

---

## 🏗️ Dependências de Produção

### Runtime Requirements

```bash
# Sistema Operacional
✓ Linux (Ubuntu/Debian) ou Windows Server

# Java Runtime
✓ JRE 21 (mínimo)
✓ JDK 21 para build

# Infraestrutura
✓ PostgreSQL 14+
✓ Redis 7+
✓ MinIO (latest stable)

# Recursos Recomendados
✓ CPU: 2+ cores
✓ RAM: 2GB+ (aplicação)
✓ Disco: 10GB+ (logs + dados)
✓ Network: Acesso HTTP/HTTPS
```

### Portas Utilizadas

| Serviço | Porta | Protocolo | Interno/Externo |
|---------|-------|-----------|-----------------|
| Music API | 8080 | HTTP | Externo |
| PostgreSQL | 5432 | TCP | Interno |
| Redis | 6379 | TCP | Interno |
| MinIO API | 9000 | HTTP | Interno/Externo |
| MinIO Console | 9001 | HTTP | Externo* |

*Recomenda-se expor o MinIO Console apenas em redes confiáveis

---

## 🐳 Dockerfile Multi-Stage

### Estrutura

O Dockerfile utiliza **multi-stage build** para otimização:

```dockerfile
Stage 1 (builder): Maven + JDK 21
  ├─ Baixa dependências
  ├─ Compila código-fonte
  └─ Gera JAR executável

Stage 2 (runtime): Ubuntu 22.04 + OpenJDK 21 JRE
  ├─ Copia apenas JAR do stage 1
  ├─ Configura usuário não-root
  ├─ Define variáveis de ambiente
  └─ Otimiza JVM para containers
```

### Benefícios

- **Segurança:** Usuário não-root, imagem base estável (Ubuntu LTS)
- **Performance:** JRE slim, JVM otimizada para containers
- **Tamanho:** ~300MB (Ubuntu + JRE)
- **Cache:** Layers otimizadas para rebuild rápido
- **Produção:** Health checks, logging, timezone configurados

### Build da Imagem

```bash
# Build simples
docker build -t music-api:latest .

# Build com tags múltiplas
docker build -t music-api:latest -t music-api:1.0.0 -t music-api:prod .

# Build com cache do Docker BuildKit
DOCKER_BUILDKIT=1 docker build -t music-api:latest .

# Verificar tamanho
docker images music-api
```

### Variáveis de Ambiente

Todas as configurações são externalizadas via environment variables:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/padawan_api
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=***

# Redis
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379

# MinIO
MINIO_URL=http://minio:9000
MINIO_ACCESS_KEY=***
MINIO_SECRET_KEY=***

# JWT (CRÍTICO!)
JWT_SECRET=*** (mínimo 256 bits)
JWT_EXPIRATION=300000
JWT_REFRESH_EXPIRATION=604800000

# JVM
JAVA_OPTS=-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0
```

---

## 🚀 Deploy em Produção

### Opção 1: Docker Compose (Recomendado)

```bash
# 1. Configurar variáveis de ambiente
cp .env.production .env
nano .env  # Editar com valores reais

# 2. Deploy completo
docker-compose -f docker-compose.production.yml up -d

# 3. Verificar status
docker-compose -f docker-compose.production.yml ps

# 4. Acompanhar logs
docker-compose -f docker-compose.production.yml logs -f music-api

# 5. Acessar aplicação
# API: http://seu-servidor:8080/api
# Swagger: http://seu-servidor:8080/api/swagger-ui.html
```

### Opção 2: Kubernetes (Avançado)

```bash
# Criar namespace
kubectl create namespace music-api

# Deploy PostgreSQL (usar Helm ou operador)
helm install postgres bitnami/postgresql -n music-api

# Deploy Redis
helm install redis bitnami/redis -n music-api

# Deploy MinIO
helm install minio bitnami/minio -n music-api

# Deploy Music API
kubectl apply -f k8s/deployment.yaml -n music-api
kubectl apply -f k8s/service.yaml -n music-api
kubectl apply -f k8s/ingress.yaml -n music-api
```

### Opção 3: Container Standalone

```bash
# 1. Build imagem
docker build -t music-api:prod .

# 2. Executar container
docker run -d \
  --name music-api \
  -p 8080:8080 \
  --env-file .env \
  --restart unless-stopped \
  --memory="2g" \
  --cpus="2.0" \
  --health-cmd="curl -f http://localhost:8080/api/v1/auth/health || exit 1" \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  music-api:prod
```

---

## 🔒 Segurança

### Checklist de Segurança

#### Antes do Deploy

- [ ] Alterar **TODAS** as senhas padrão
- [ ] Gerar JWT_SECRET forte (mínimo 256 bits)
- [ ] Configurar HTTPS/TLS (certificado SSL)
- [ ] Revisar permissões de usuários do banco
- [ ] Desabilitar console MinIO em produção (ou proteger)
- [ ] Configurar firewall (permitir apenas portas necessárias)
- [ ] Implementar rate limiting
- [ ] Configurar CORS adequadamente
- [ ] Habilitar audit logs

#### Configuração Segura

```bash
# Gerar JWT secret forte
openssl rand -base64 64

# Criar senha PostgreSQL
openssl rand -base64 32

# Criar senha MinIO
openssl rand -base64 32
```

#### Hardening do Container

```yaml
# docker-compose.production.yml
music-api:
  security_opt:
    - no-new-privileges:true
  cap_drop:
    - ALL
  cap_add:
    - NET_BIND_SERVICE
  read_only: true
  tmpfs:
    - /tmp
    - /app/temp
```

#### Segredos (Secrets)

Use **Docker Secrets** ou **Kubernetes Secrets**:

```bash
# Docker Swarm Secrets
echo "minha_senha_super_secreta" | docker secret create db_password -
echo "jwt_secret_256bits_aqui" | docker secret create jwt_secret -

# Kubernetes Secrets
kubectl create secret generic music-api-secrets \
  --from-literal=db-password='***' \
  --from-literal=jwt-secret='***' \
  -n music-api
```

---

## 📊 Monitoramento

### Health Checks

```bash
# Aplicação
curl http://localhost:8080/api/v1/auth/health

# PostgreSQL
docker exec music-api-postgres pg_isready -U postgres

# Redis
docker exec music-api-redis redis-cli ping

# MinIO
curl http://localhost:9000/minio/health/live
```

### Logs

```bash
# Docker Compose
docker-compose -f docker-compose.production.yml logs -f

# Container específico
docker logs -f music-api-app --tail 100

# Filtrar por nível
docker logs music-api-app 2>&1 | grep ERROR

# Exportar logs
docker logs music-api-app > logs/$(date +%Y%m%d).log
```

### Métricas

Adicionar Spring Boot Actuator para métricas avançadas:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application-prod.properties
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

### Stack de Monitoramento (Opcional)

```yaml
# Prometheus + Grafana
prometheus:
  image: prom/prometheus
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml

grafana:
  image: grafana/grafana
  ports:
    - "3000:3000"
```

---

## 🛠️ Troubleshooting

### Problemas Comuns

#### Container não inicia

```bash
# Ver logs detalhados
docker logs music-api-app

# Verificar variáveis de ambiente
docker exec music-api-app env | grep SPRING

# Testar conectividade
docker exec music-api-app ping postgres
docker exec music-api-app nc -zv redis 6379
```

#### Erro de conexão com banco

```bash
# Verificar se PostgreSQL está rodando
docker ps | grep postgres

# Testar conexão direta
docker exec -it music-api-postgres psql -U postgres -d padawan_api

# Verificar migrations
docker exec music-api-app cat /app/logs/liquibase.log
```

#### Erro de autenticação JWT

```bash
# Verificar se JWT_SECRET está configurado
docker exec music-api-app env | grep JWT_SECRET

# Validar tamanho do secret (mínimo 256 bits = 32 bytes)
echo -n "seu_secret" | wc -c
```

#### Performance ruim

```bash
# Verificar uso de recursos
docker stats music-api-app

# Aumentar memória JVM
docker run -e JAVA_OPTS="-Xmx2g -Xms1g" ...

# Analisar threads
docker exec music-api-app jstack 1
```

### Comandos Úteis

```bash
# Shell no container
docker exec -it music-api-app sh

# Verificar processos
docker exec music-api-app ps aux

# Verificar disco
docker exec music-api-app df -h

# Network troubleshooting
docker exec music-api-app netstat -tlnp

# Backup banco de dados
docker exec music-api-postgres pg_dump -U postgres padawan_api > backup.sql

# Restaurar banco
cat backup.sql | docker exec -i music-api-postgres psql -U postgres padawan_api
```

---

## 📚 Referências

- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Docker Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)
- [PostgreSQL Docker](https://hub.docker.com/_/postgres)
- [Redis Docker](https://hub.docker.com/_/redis)
- [MinIO Docker](https://min.io/docs/minio/container/index.html)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)

---

## 🎯 Checklist Final de Deploy

- [ ] Código testado e versionado (git tag)
- [ ] Build do JAR bem-sucedido
- [ ] Dockerfile validado
- [ ] Variáveis de ambiente configuradas (.env)
- [ ] Senhas e secrets alterados
- [ ] Infraestrutura provisionada (PostgreSQL, Redis, MinIO)
- [ ] Certificado SSL configurado (HTTPS)
- [ ] Firewall configurado
- [ ] Backup configurado
- [ ] Monitoramento ativo
- [ ] Logs centralizados
- [ ] Documentação atualizada
- [ ] Runbook de incidentes preparado
- [ ] Plano de rollback definido

---

**Boa sorte com o deploy! 🚀**
