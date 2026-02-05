# JWT Authentication Setup - Music API

## Overview
The Music API has been refactored to use **JWT (JSON Web Token) authentication** instead of HTTP Basic Auth. This document explains the new authentication flow and how to test it.

---

## What Has Changed

### Before (Old Configuration)
- HTTP Basic Authentication (username:password in Base64)
- All `/v1/**` endpoints were public
- No token-based authentication

### After (New Configuration)
- **JWT Token-based Authentication**
- Login endpoint to obtain tokens
- Protected API endpoints requiring valid JWT tokens
- Swagger UI accessible with or without tokens
- Default admin user created automatically

---

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    roles VARCHAR(50) NOT NULL DEFAULT 'USER'
);
```

**Default Admin User:**
- Username: `admin`
- Password: `admin123`
- Role: `ADMIN`

---

## Authentication Flow

### 1. Login (Get JWT Token)
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}

Response (HTTP 200):
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "admin"
}
```

### 2. Use Token to Access Protected Endpoints
Include the token in the `Authorization` header:
```
GET /api/v1/artistas
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. Token Validation
- Tokens expire after **24 hours** (configurable via `jwt.expiration`)
- Invalid/expired tokens return **401 Unauthorized**

---

## Quick Start

### Step 1: Build & Run the Application
```bash
./mvnw clean package
./mvnw spring-boot:run
```

The application will:
1. Create the `users` table (Liquibase migration)
2. Insert the default admin user
3. Start the Spring Boot server on port 8080

### Step 2: Access Login Page
Open in your browser:
```
http://localhost:8080/api/login
```

### Step 3: Login
1. Enter username: `admin`
2. Enter password: `admin123`
3. Click "Login"
4. Copy the JWT token displayed

### Step 4: Access Swagger UI with Token
1. Go to: `http://localhost:8080/api/v1/swagger-ui.html`
2. Click the "Authorize" button (top-right)
3. Paste your token in format: `Bearer <your-token>`
4. Click "Authorize" and then "Close"
5. Now you can test all API endpoints in Swagger

---

## Public Endpoints (No Authentication Required)

- `/login` — Login page
- `/v1/auth/login` — Get JWT token
- `/v1/auth/register` — Register new user
- `/swagger-ui/**` — Swagger UI
- `/v3/api-docs/**` — OpenAPI documentation
- `/swagger-resources/**` — Swagger resources
- `/webjars/**` — Web JAR resources

---

## Protected Endpoints (Require JWT Token)

All other endpoints under `/v1/**` require a valid JWT token in the `Authorization` header.

Example:
```bash
curl -H "Authorization: Bearer <your-token>" \
  http://localhost:8080/api/v1/artistas
```

---

## Configuration Properties

Located in `src/main/resources/application.properties`:

```properties
# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidationPurposesOnly12345678
jwt.expiration=86400000  # 24 hours in milliseconds

# Environment variables (override defaults):
# JWT_SECRET=your-secret-key
# JWT_EXPIRATION=86400000
```

**Important for Production:**
- Change `jwt.secret` to a long, random string
- Use environment variables to set secrets securely
- Increase `jwt.expiration` if needed (in milliseconds)

---

## New Classes & Components

### Authentication Components
| File | Purpose |
|------|---------|
| `JwtTokenProvider.java` | Generate and validate JWT tokens |
| `JwtAuthenticationFilter.java` | Extract and validate tokens from requests |
| `CustomUserDetailsService.java` | Load user details from database |
| `SecurityConfig.java` | Spring Security configuration with JWT |

### Controllers
| File | Purpose |
|------|---------|
| `AuthController.java` | Login and register endpoints |
| `LoginController.java` | Serves the login page |

### Domain & Repository
| File | Purpose |
|------|---------|
| `Domain/User.java` | User entity |
| `Repository/UserRepository.java` | User persistence |

### DTOs
| File | Purpose |
|------|---------|
| `Controller/dto/LoginRequest.java` | Login request payload |
| `Controller/dto/JwtAuthResponse.java` | JWT response payload |

### Database Migrations
| File | Purpose |
|------|---------|
| `db.migracao/002-create-users-table.xml` | Create users table and default admin user |

---

## Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Register New User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"password123"}'
```

### Access Protected Endpoint with Token
```bash
# First, get the token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.accessToken')

# Use the token
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/artistas
```

---

## Troubleshooting

### "Invalid credentials" on login
- Check username and password in the database
- Ensure the users table was created by Liquibase
- Verify the BCrypt hash matches the password

### "Unable to generate JWT token" errors
- Check that `jwt.secret` property is set
- Verify java version is 11+
- Check logs for JJWT library errors

### Token not working in Swagger UI
- Ensure token format is: `Bearer <token>` (with space)
- Check that token hasn't expired (default 24 hours)
- Try refreshing page and authorizing again

### Users table not created
- Check Liquibase logs during startup
- Verify PostgreSQL is running and accessible
- Review `src/main/resources/db/changelog/db.migracao/002-create-users-table.xml`

---

## Production Checklist

- [ ] Change `jwt.secret` to a strong random key
- [ ] Update `jwt.expiration` as needed
- [ ] Use environment variables to manage secrets
- [ ] Enable HTTPS only
- [ ] Add rate limiting to login endpoint
- [ ] Implement refresh tokens
- [ ] Add token expiration refresh mechanism
- [ ] Setup audit logging for authentication events
- [ ] Configure CORS if calling from different domain
- [ ] Add user account lockout after failed attempts

---

## Dependencies Added

The following JWT library was added to `pom.xml`:

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Client Request                        │
└────────────────────┬────────────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
    ┌────▼─────┐          ┌──────▼──────┐
    │Login (No │          │API Request  │
    │Auth)     │          │(with JWT)   │
    └────┬─────┘          └──────┬──────┘
         │                       │
    ┌────▼──────────────┬───────┴──────┐
    │AuthController     │JwtAuthFilter │
    │.login()           │(Intercepts)  │
    └────┬──────────────┴───────┬──────┘
         │                      │
    ┌────▼────────────────┐ ┌───▼──────────────┐
    │UserDetailsService   │ │JwtTokenProvider  │
    │.authenticate()      │ │.validateToken()  │
    └────┬────────────────┘ └───┬──────────────┘
         │                      │
    ┌────▼──────────────────────▼──────────┐
    │PostgreSQL (users table)              │
    └──────────────────────────────────────┘
```

---

## Support

For issues or questions, review:
1. Application logs: `logs/application.log`
2. SecurityConfig for endpoint permissions
3. Database migration files in `db.migracao/`
4. JJWT library documentation: https://github.com/jwtk/jjwt
