# Autenticação JWT - Resumo

## ✅ Implementação Completa

O SecurityConfig.java foi criado para usar **autenticação JWT (JSON Web Token)** com um sistema de login seguro.

---

## 📋 O Que Foi Implementado

### 1. **Provedor de Token JWT** (`JwtTokenProvider.java`)
   - Gera **access tokens** JWT com expiração de **5 minutos** (300000ms)
   - Gera **refresh tokens** JWT com expiração de **7 dias** (604800000ms)
   - Valida assinaturas e expiração de tokens
   - Extrai nome de usuário dos tokens
   - Usa biblioteca JJWT 0.12.3 com HMAC-SHA256
   - Refresh tokens incluem claim `type: "refresh"` para identificação

### 2. **Filtro de Autenticação JWT** (`JwtAuthenticationFilter.java`)
   - Intercepta requisições HTTP
   - Extrai token JWT do cabeçalho `Authorization: Bearer <token>`
   - Valida token e define o principal autenticado

### 3. **Gerenciamento de Usuários**
   - **Entidade User** (`Domain/User.java`): Armazena username, password, email, roles
   - **UserRepository** (`Repository/UserRepository.java`): Interface JPA para persistência de usuários
   - **CustomUserDetailsService**: Carrega usuários do banco de dados com autenticação de roles

### 4. **Endpoints de Autenticação** (`AuthController.java`)
   - `POST /v1/auth/login` — Autenticar e obter access e refresh tokens
   - `POST /v1/auth/register` — Registrar novos usuários
   - `POST /v1/auth/refresh` — Renovar access token usando refresh token válido

### 5. **Página de Login** (`templates/login.html`)
   - UI de login bonita e responsiva
   - Autenticação com username/password
   - Suporte a registro de usuários
   - Exibe token JWT após login bem-sucedido
   - Funcionalidade de copiar para área de transferência para tokens
   - Link direto para Swagger UI com instruções de autorização

### 6. **Controller da Página de Login** (`LoginController.java`)
   - Serve a página de login em `/login`

### 7. **SecurityConfig Atualizado** (`Config/SecurityConfig.java`)
   - CSRF desabilitado (apropriado para APIs REST)
   - Gerenciamento de sessão stateless configurado
   - Filtro JWT integrado na cadeia de filtros
   - Protegidos todos os endpoints exceto:
     - Endpoints de Login/Registro
     - Documentação Swagger/OpenAPI
     - Página de login

### 8. **Migrações de Banco de Dados** (`db.migracao/002-create-users-table.xml`)
   - Cria tabela `users` com esquema apropriado
   - Insere usuário admin padrão (username: admin, password: admin)

### 9. **Dependências Adicionadas** (`pom.xml`)
   - JJWT API, Implementation e suporte Jackson (versão 0.12.3)

### 10. **Configuração** (`application.properties`)
   - Configuração de chave secreta JWT
   - Tempo de expiração do access token (5 minutos - 300000ms)
   - Tempo de expiração do refresh token (7 dias - 604800000ms)
   - Suporte a override de variável de ambiente

### 11. **DTOs Adicionais**
   - **RefreshTokenRequest** (`Controller/dto/RefreshTokenRequest.java`): Payload para renovação de token
   - **JwtAuthResponse** atualizado com campos `refreshToken` e `expiresIn`

---

## 🚀 Início Rápido

### 1. Build & Executar
```bash
./mvnw clean package
./mvnw spring-boot:run
```

### 2. Login
- Abra: `http://localhost:8080/api/login`
- Username: `admin`
- Password: `admin123`
- Copie o token JWT exibido

### 3. Usar Swagger com JWT
1. Acesse: `http://localhost:8080/api/v1/swagger-ui.html`
2. Clique no botão "Authorize" (canto superior direito)
3. Cole: `Bearer <seu-token>` (com a palavra "Bearer" antes do token)
4. Clique em "Authorize" e depois em "Close"
5. Agora teste os endpoints da API com autenticação

### 4. Testar Endpoints Protegidos
```bash
curl -H "Authorization: Bearer <seu-token>" \
  http://localhost:8080/api/v1/artistas
```

---

## 🔒 Recursos de Segurança

✅ **Autenticação JWT** - Autenticação stateless baseada em token  
✅ **Hashing de Senha BCrypt** - Senhas criptografadas com BCrypt  
✅ **Access Token de Curta Duração** - 5 minutos para maior segurança  
✅ **Refresh Token de Longa Duração** - 7 dias para conveniência do usuário  
✅ **Renovação Automática de Token** - Endpoint de refresh para renovar tokens expirados  
✅ **Controle de Acesso Baseado em Roles** - Usuários podem ter múltiplas roles  
✅ **Sem Estado de Sessão** - Perfeito para microsserviços e escalabilidade  
✅ **Validação de Assinatura de Token** - Previne adulteração de tokens  

---

## 📁 Arquivos 

### Arquivos Criados:
- `src/main/java/br/com/music/api/Domain/User.java`
- `src/main/java/br/com/music/api/Repository/UserRepository.java`
- `src/main/java/br/com/music/api/Config/JwtTokenProvider.java`
- `src/main/java/br/com/music/api/Config/JwtAuthenticationFilter.java`
- `src/main/java/br/com/music/api/Config/CustomUserDetailsService.java`
- `src/main/java/br/com/music/api/Controller/AuthController.java`
- `src/main/java/br/com/music/api/Controller/LoginController.java`
- `src/main/java/br/com/music/api/Controller/dto/LoginRequest.java`
- `src/main/java/br/com/music/api/Controller/dto/JwtAuthResponse.java`
- `src/main/java/br/com/music/api/Controller/dto/RefreshTokenRequest.java`
- `src/main/resources/templates/login.html`
- `src/main/resources/db/changelog/db.migracao/002-create-users-table.xml`
- `JWT_AUTHENTICATION_GUIDE.md` — Documentação abrangente

### Arquivos Modificados
- `pom.xml` — Dependências JJWT
- `src/main/java/br/com/music/api/Config/SecurityConfig.java` — Codificado para uso do JWT
- `src/main/resources/application.properties` — Adicionada configuração JWT
- `src/main/resources/db/changelog/db.master.xml` — Adicionada referência de migração

---

## 🔑 Credenciais Padrão

Após executar a aplicação:
- **Username**: `admin`
- **Password**: `admin123`

⚠️ **Importante**: Altere estas credenciais em produção!

---

## 📖 Documentação Adicional

Veja `JWT_AUTHENTICATION_GUIDE.md` para:
- Diagrama completo do fluxo de autenticação
- Exemplos de teste com cURL
- Guia de solução de problemas
- Checklist de produção
- Visão geral da arquitetura

---

## ⚡ Próximos Passos

1. **Testar a Página de Login**: Abra `http://localhost:8080/api/login`
2. **Obter Tokens**: Faça login com admin/admin123
3. **Testar Endpoints da API**: Use o access token no Swagger UI
4. **Testar Renovação**: Após 5 minutos, use o refresh token para obter novo access token
5. **Criar Novos Usuários**: Use o endpoint `/v1/auth/register`
6. **Personalizar**: Altere a chave secreta e tempos de expiração conforme necessário

---

## 🎯 Verification Checklist

- ✅ Code compiles without errors
- ✅ JWT token generation functional
- ✅ Database migration ready (creates users table)
- ✅ Login page created with registration support
- ✅ Swagger UI accessible with and without tokens
- ✅ Protected endpoints require valid JWT
- ✅ Token validation implemented
- ✅ Default admin user provisioned
- ✅ Comprehensive documentation provided

---

## 🔄 Fluxo de Arquitetura

```
Login do Usuário → AuthController → CustomUserDetailsService → Banco de Dados
            ↓
    Gerar Access Token (5 min) + Refresh Token (7 dias)
            ↓
Requisição do Usuário com Access Token → JwtAuthenticationFilter → Validar & Extrair Username
            ↓
        Verificar Validade do Token & Definir SecurityContext
            ↓
Acessar Endpoints REST Protegidos (Swagger ou API)
            ↓
Access Token Expirado? → POST /v1/auth/refresh com Refresh Token
            ↓
    Validar Refresh Token → Gerar Novos Access + Refresh Tokens
            ↓
        Continuar Acessando Endpoints Protegidos
```

---

Pronto para usar! Para documentação detalhada, veja `JWT_AUTHENTICATION_GUIDE.md`
