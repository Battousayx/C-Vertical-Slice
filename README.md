# Music API - Guia de Dependências, Arquitetura e Padrões

## Dependências Necessárias (instalação local)

### Essenciais
- Java 21 (JDK)
- Maven (opcional, o projeto usa Maven Wrapper via `./mvnw`)
- Git

### Infra local (recomendado via Docker)
- Docker Engine
- Docker Compose (plugin `docker compose`)

### Serviços externos exigidos em runtime
- PostgreSQL (porta padrão 5432)
- Redis (porta padrão 6379)
- MinIO (porta padrão 9000/9001)

> Observação: o projeto inclui um `docker-compose.yml` em `src/main/resources/services/docker` para subir Postgres, Redis e MinIO localmente.

## Arquitetura do Projeto

O projeto segue uma arquitetura em camadas com separação clara de responsabilidades:

- **Controller**: endpoints REST e mapeamento de request/response.
- **Services**: regras de negócio e fronteiras transacionais.
- **Repository**: acesso a dados via Spring Data JPA.
- **Domain**: entidades JPA e enums do domínio.
- **Config**: configurações de segurança, OpenAPI e integrações (ex.: MinIO).

Estrutura principal:

- `src/main/java/br/com/music/api/Controller`
- `src/main/java/br/com/music/api/Services`
- `src/main/java/br/com/music/api/Repository`
- `src/main/java/br/com/music/api/Domain`
- `src/main/java/br/com/music/api/Config`

## Metodologia de Desenvolvimento

- **API REST**: endpoints HTTP com semântica REST.
- **Separação de camadas**: Controllers delegam para Services; Services interagem com Repositories.
- **Configuração via properties**: credenciais e endpoints configurados em `application.properties`.
- **Evolução de banco via migration**: Liquibase gerencia alterações de schema.

## Padrões de Projeto Utilizados

- **Repository Pattern**: interfaces JPA para persistência (Spring Data).
- **Service Layer**: encapsula regras de negócio e transações.
- **DTO + Mapper (quando aplicável)**: isolamento de entidades do domínio nas respostas.
- **Dependency Injection**: componentes gerenciados pelo Spring (`@Service`, `@Repository`, `@Configuration`).
- **Configuration Pattern**: beans centralizados para clientes externos (ex.: MinIO).

## Autenticação e Segurança

O projeto implementa **autenticação JWT (JSON Web Token)** para proteger endpoints da API.

### Features de Segurança
- ✅ Autenticação baseada em JWT com expiração configurável (24 horas por padrão)
- ✅ Hashing de senhas com BCrypt
- ✅ Controle de acesso baseado em roles
- ✅ Página de login responsiva (em português)
- ✅ Integração com Swagger UI para testes autenticados
- ✅ Gerenciamento de usuários com registro e login

### Documentação de Autenticação
Para detalhes completos sobre a implementação de JWT, consulte:
- **[JWT_AUTHENTICATION_GUIDE.md](JWT_AUTHENTICATION_GUIDE.md)** - Guia completo com fluxo de autenticação, testes com cURL, solução de problemas e checklist de produção
- **[JWT_IMPLEMENTATION_SUMMARY.md](JWT_IMPLEMENTATION_SUMMARY.md)** - Resumo técnico da implementação com arquitetura e componentes utilizados

### Acesso Rápido
- **Página de Login**: `http://localhost:8080/api/login` (Clique em registre-se Já informe um usuário e senha e volte a tela de login)
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **MinIO**: `http://localhost:9000` (access key: `admin`, secret key: `admin123`)

## Armazenamento de Arquivos (MinIO)

O projeto utiliza **MinIO** como solução de armazenamento de objetos para gerenciar imagens de álbuns e outros arquivos.

### Features de Armazenamento
- ✅ Upload de imagens para MinIO
- ✅ Download e visualização de imagens
- ✅ Conversão para Base64
- ✅ Integração com banco de dados (referências em `album_imagem`)
- ✅ Suporte a múltiplas imagens por álbum

### Documentação de MinIO
Para detalhes completos sobre testes e uso do MinIO, consulte:
- **[MINIO_TEST_GUIDE.md](MINIO_TEST_GUIDE.md)** - Guia completo de testes com MinIO, incluindo exemplos práticos de upload/download, integração com banco de dados, testes via cURL e troubleshooting

### Configuração MinIO
As configurações do MinIO estão em `application.properties`:
```properties
minio.access.key=admin
minio.secret.key=admin123
minio.url=http://localhost:9000
minio.bucket.name=meu-bucket
```

## Como Rodar (comandos)

### Opção 1: Docker Compose (Recomendado) 🐳

Execute toda a infraestrutura (PostgreSQL + Redis + MinIO + API) com um único comando:

#### Usando o Script de Deploy (Mais Fácil)

```bash
# Deploy completo (build JAR + build Docker + start)
./deploy.sh deploy

# Ou individual:
./deploy.sh build    # Build do JAR
./deploy.sh start    # Iniciar serviços
./deploy.sh logs     # Ver logs
./deploy.sh stop     # Parar serviços
./deploy.sh status   # Verificar status
./deploy.sh clean    # Limpar tudo
```

#### Manualmente

```bash
# 1. Build da aplicação
./mvnw clean package -DskipTests
cp target/music-api-0.0.1-SNAPSHOT.jar music-app.jar

# 2. Subir todos os serviços
docker-compose up -d

# 3. Acompanhar logs
docker-compose logs -f music-api
```

**Acessos:**
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/api/swagger-ui.html
- Login: http://localhost:8080/api/login
- MinIO Console: http://localhost:9001 (admin/admin123)

📖 **Documentação completa:** [README_DOCKER.md](README_DOCKER.md)

### Opção 2: Infraestrutura Docker + Aplicação Local

Subir infraestrutura local (Docker):

```bash
docker compose -f src/main/resources/services/docker/docker-compose.yml up -d
```

Rodar a aplicação localmente:

```bash
./mvnw spring-boot:run
```

### Opção 3: Somente com JAR

```bash
# Build
./mvnw clean package -DskipTests

# Executar (requer PostgreSQL, Redis e MinIO rodando)
java -jar target/music-api-0.0.1-SNAPSHOT.jar
```

## Build e Testes

### Build do projeto

```bash
# Build completo com testes
./mvnw clean package

# Build sem testes (mais rápido)
./mvnw clean package -DskipTests
```

### Executar testes

```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=MinioStorageServiceTest
./mvnw test -Dtest=ArtistaServiceTest
```

### Gerar music-app.jar

```bash
./mvnw clean package -DskipTests
cp target/music-api-0.0.1-SNAPSHOT.jar music-app.jar
```

## Observações Importantes

- As migrations ficam em `src/main/resources/db/changelog`.
- O bucket e as credenciais do MinIO são configurados em `application.properties`.
- O contexto base do servidor é `/api` (ver `application.properties`).
