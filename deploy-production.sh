#!/bin/bash

# ================================
# Music API - Production Deploy Script
# ================================
# Script automatizado para deploy em produção
# Uso: ./deploy-production.sh [comando]

set -e  # Exit on error

# ================================
# Cores para output
# ================================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# ================================
# Configurações
# ================================
DOCKER_IMAGE="music-api"
DOCKER_TAG="latest"
COMPOSE_FILE="docker-compose.production.yml"
ENV_FILE=".env"

# ================================
# Banner
# ================================
show_banner() {
    echo -e "${BLUE}"
    echo "╔═══════════════════════════════════════════════╗"
    echo "║     Music API - Production Deployment        ║"
    echo "║            v1.0.0                             ║"
    echo "╚═══════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# ================================
# Mostrar uso
# ================================
show_usage() {
    echo -e "${CYAN}Uso:${NC} $0 [comando]"
    echo ""
    echo -e "${CYAN}Comandos disponíveis:${NC}"
    echo "  ${GREEN}check${NC}      - Verificar pré-requisitos"
    echo "  ${GREEN}build${NC}      - Build da imagem Docker"
    echo "  ${GREEN}deploy${NC}     - Deploy completo (build + start)"
    echo "  ${GREEN}start${NC}      - Iniciar serviços"
    echo "  ${GREEN}stop${NC}       - Parar serviços"
    echo "  ${GREEN}restart${NC}    - Reiniciar serviços"
    echo "  ${GREEN}status${NC}     - Status dos serviços"
    echo "  ${GREEN}logs${NC}       - Ver logs da aplicação"
    echo "  ${GREEN}health${NC}     - Verificar health de todos os serviços"
    echo "  ${GREEN}backup${NC}     - Backup do banco de dados"
    echo "  ${GREEN}clean${NC}      - Limpar containers e volumes"
    echo "  ${GREEN}security${NC}   - Verificar configurações de segurança"
    echo ""
    echo -e "${CYAN}Exemplos:${NC}"
    echo "  $0 deploy          # Deploy completo"
    echo "  $0 logs            # Ver logs em tempo real"
    echo "  $0 backup          # Criar backup do PostgreSQL"
}

# ================================
# Verificar pré-requisitos
# ================================
check_prerequisites() {
    echo -e "${YELLOW}→ Verificando pré-requisitos...${NC}"
    
    local errors=0
    
    # Docker
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}✗ Docker não encontrado${NC}"
        errors=$((errors + 1))
    else
        echo -e "${GREEN}✓ Docker instalado: $(docker --version | cut -d' ' -f3)${NC}"
    fi
    
    # Docker Compose
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        echo -e "${RED}✗ Docker Compose não encontrado${NC}"
        errors=$((errors + 1))
    else
        echo -e "${GREEN}✓ Docker Compose instalado${NC}"
    fi
    
    # .env file
    if [ ! -f "$ENV_FILE" ]; then
        echo -e "${YELLOW}⚠ Arquivo .env não encontrado${NC}"
        echo -e "${YELLOW}  Copie .env.production para .env e configure${NC}"
        errors=$((errors + 1))
    else
        echo -e "${GREEN}✓ Arquivo .env encontrado${NC}"
    fi
    
    # Dockerfile
    if [ ! -f "Dockerfile" ]; then
        echo -e "${RED}✗ Dockerfile não encontrado${NC}"
        errors=$((errors + 1))
    else
        echo -e "${GREEN}✓ Dockerfile encontrado${NC}"
    fi
    
    # Docker Compose file
    if [ ! -f "$COMPOSE_FILE" ]; then
        echo -e "${RED}✗ $COMPOSE_FILE não encontrado${NC}"
        errors=$((errors + 1))
    else
        echo -e "${GREEN}✓ $COMPOSE_FILE encontrado${NC}"
    fi
    
    if [ $errors -gt 0 ]; then
        echo -e "${RED}✗ Pré-requisitos não atendidos ($errors erros)${NC}"
        exit 1
    else
        echo -e "${GREEN}✓ Todos os pré-requisitos atendidos${NC}"
    fi
}

# ================================
# Verificar segurança
# ================================
check_security() {
    echo -e "${YELLOW}→ Verificando configurações de segurança...${NC}"
    
    local warnings=0
    
    if [ -f "$ENV_FILE" ]; then
        # Verificar senhas padrão
        if grep -q "changeme" "$ENV_FILE"; then
            echo -e "${RED}✗ Senhas padrão 'changeme' detectadas no .env${NC}"
            warnings=$((warnings + 1))
        fi
        
        # Verificar JWT secret
        if grep -q "JWT_SECRET=your_super_secret" "$ENV_FILE" || grep -q "JWT_SECRET=changeme" "$ENV_FILE"; then
            echo -e "${RED}✗ JWT_SECRET usando valor padrão - ALTERE IMEDIATAMENTE!${NC}"
            warnings=$((warnings + 1))
        else
            echo -e "${GREEN}✓ JWT_SECRET configurado${NC}"
        fi
        
        # Verificar tamanho do JWT secret
        jwt_secret=$(grep "^JWT_SECRET=" "$ENV_FILE" | cut -d'=' -f2)
        if [ ${#jwt_secret} -lt 32 ]; then
            echo -e "${YELLOW}⚠ JWT_SECRET muito curto (recomendado: 256 bits / 32+ caracteres)${NC}"
            warnings=$((warnings + 1))
        fi
        
        # Verificar senha do PostgreSQL
        if grep -q "POSTGRES_PASSWORD=admin" "$ENV_FILE" || grep -q "POSTGRES_PASSWORD=postgres" "$ENV_FILE"; then
            echo -e "${RED}✗ Senha do PostgreSQL usando valor padrão${NC}"
            warnings=$((warnings + 1))
        fi
    fi
    
    if [ $warnings -gt 0 ]; then
        echo -e "${YELLOW}⚠ $warnings avisos de segurança encontrados${NC}"
        echo -e "${YELLOW}  Revise as configurações antes de deploy em produção!${NC}"
    else
        echo -e "${GREEN}✓ Configurações de segurança OK${NC}"
    fi
}

# ================================
# Build da imagem
# ================================
build_image() {
    echo -e "${YELLOW}→ Building Docker image...${NC}"
    
    docker-compose -f "$COMPOSE_FILE" build --no-cache music-api
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Imagem construída com sucesso${NC}"
        docker images "$DOCKER_IMAGE"
    else
        echo -e "${RED}✗ Erro ao construir imagem${NC}"
        exit 1
    fi
}

# ================================
# Iniciar serviços
# ================================
start_services() {
    echo -e "${YELLOW}→ Iniciando serviços...${NC}"
    
    docker-compose -f "$COMPOSE_FILE" up -d
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Serviços iniciados${NC}"
        echo ""
        sleep 5
        show_status
        show_access_info
    else
        echo -e "${RED}✗ Erro ao iniciar serviços${NC}"
        exit 1
    fi
}

# ================================
# Parar serviços
# ================================
stop_services() {
    echo -e "${YELLOW}→ Parando serviços...${NC}"
    
    docker-compose -f "$COMPOSE_FILE" down
    
    echo -e "${GREEN}✓ Serviços parados${NC}"
}

# ================================
# Status dos serviços
# ================================
show_status() {
    echo -e "${CYAN}Status dos serviços:${NC}"
    docker-compose -f "$COMPOSE_FILE" ps
}

# ================================
# Logs
# ================================
show_logs() {
    echo -e "${CYAN}Logs da aplicação (Ctrl+C para sair):${NC}"
    docker-compose -f "$COMPOSE_FILE" logs -f music-api
}

# ================================
# Health check
# ================================
check_health() {
    echo -e "${YELLOW}→ Verificando health dos serviços...${NC}"
    
    # PostgreSQL
    echo -n "PostgreSQL: "
    if docker exec music-api-postgres pg_isready -U postgres &> /dev/null; then
        echo -e "${GREEN}✓ Healthy${NC}"
    else
        echo -e "${RED}✗ Unhealthy${NC}"
    fi
    
    # Redis
    echo -n "Redis: "
    if docker exec music-api-redis redis-cli ping &> /dev/null; then
        echo -e "${GREEN}✓ Healthy${NC}"
    else
        echo -e "${RED}✗ Unhealthy${NC}"
    fi
    
    # MinIO
    echo -n "MinIO: "
    if curl -f http://localhost:9000/minio/health/live &> /dev/null; then
        echo -e "${GREEN}✓ Healthy${NC}"
    else
        echo -e "${RED}✗ Unhealthy${NC}"
    fi
    
    # Music API
    echo -n "Music API: "
    if curl -f http://localhost:8080/api/v1/auth/health &> /dev/null; then
        echo -e "${GREEN}✓ Healthy${NC}"
    else
        echo -e "${RED}✗ Unhealthy (pode estar inicializando)${NC}"
    fi
}

# ================================
# Backup
# ================================
backup_database() {
    echo -e "${YELLOW}→ Criando backup do banco de dados...${NC}"
    
    BACKUP_DIR="backups"
    mkdir -p "$BACKUP_DIR"
    
    BACKUP_FILE="$BACKUP_DIR/padawan_api_$(date +%Y%m%d_%H%M%S).sql"
    
    docker exec music-api-postgres pg_dump -U postgres padawan_api > "$BACKUP_FILE"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Backup criado: $BACKUP_FILE${NC}"
        gzip "$BACKUP_FILE"
        echo -e "${GREEN}✓ Backup comprimido: ${BACKUP_FILE}.gz${NC}"
    else
        echo -e "${RED}✗ Erro ao criar backup${NC}"
        exit 1
    fi
}

# ================================
# Limpeza
# ================================
clean_all() {
    echo -e "${RED}⚠ Esta ação irá remover containers, volumes e imagens!${NC}"
    read -p "Tem certeza? (yes/no): " confirm
    
    if [ "$confirm" != "yes" ]; then
        echo "Operação cancelada."
        exit 0
    fi
    
    echo -e "${YELLOW}→ Removendo tudo...${NC}"
    docker-compose -f "$COMPOSE_FILE" down -v --rmi local
    echo -e "${GREEN}✓ Limpeza concluída${NC}"
}

# ================================
# Informações de acesso
# ================================
show_access_info() {
    echo ""
    echo -e "${GREEN}═══════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  Aplicação disponível em:${NC}"
    echo -e "${GREEN}  → API: http://localhost:8080/api${NC}"
    echo -e "${GREEN}  → Swagger: http://localhost:8080/api/swagger-ui.html${NC}"
    echo -e "${GREEN}  → Login: http://localhost:8080/api/login${NC}"
    echo -e "${GREEN}  → MinIO Console: http://localhost:9001${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════${NC}"
    echo ""
}

# ================================
# Deploy completo
# ================================
deploy_full() {
    check_prerequisites
    check_security
    build_image
    start_services
}

# ================================
# Main
# ================================
show_banner

case "$1" in
    check)
        check_prerequisites
        check_security
        ;;
    build)
        check_prerequisites
        build_image
        ;;
    deploy)
        deploy_full
        ;;
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    restart)
        stop_services
        sleep 2
        start_services
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    health)
        check_health
        ;;
    backup)
        backup_database
        ;;
    clean)
        clean_all
        ;;
    security)
        check_security
        ;;
    *)
        show_usage
        exit 1
        ;;
esac
