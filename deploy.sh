#!/bin/bash

# ================================
# Music API - Deploy Script
# ================================

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}"
echo "╔═══════════════════════════════════════╗"
echo "║     Music API - Deploy Manager        ║"
echo "╚═══════════════════════════════════════╝"
echo -e "${NC}"

# Função de uso
show_usage() {
    echo "Uso: $0 [comando]"
    echo ""
    echo "Comandos:"
    echo "  build      - Build do JAR + Docker image"
    echo "  start      - Iniciar todos os serviços"
    echo "  stop       - Parar todos os serviços"
    echo "  restart    - Reiniciar todos os serviços"
    echo "  logs       - Ver logs da aplicação"
    echo "  status     - Status dos serviços"
    echo "  clean      - Limpar containers e volumes"
    echo ""
}

# Build
build() {
    echo -e "${YELLOW}→ Building JAR...${NC}"
    ./mvnw clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        cp target/music-api-0.0.1-SNAPSHOT.jar app-music.jar
        echo -e "${GREEN}✓ JAR criado: app-music.jar${NC}"
    else
        echo -e "${RED}✗ Erro ao criar JAR${NC}"
        exit 1
    fi
    
    echo -e "${YELLOW}→ Building Docker image...${NC}"
    docker-compose build music-api
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Imagem Docker criada${NC}"
    else
        echo -e "${RED}✗ Erro ao criar imagem${NC}"
        exit 1
    fi
}

# Start
start() {
    echo -e "${YELLOW}→ Iniciando serviços...${NC}"
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Serviços iniciados${NC}"
        echo ""
        sleep 5
        show_access
    else
        echo -e "${RED}✗ Erro ao iniciar serviços${NC}"
        exit 1
    fi
}

# Stop
stop() {
    echo -e "${YELLOW}→ Parando serviços...${NC}"
    docker-compose down
    echo -e "${GREEN}✓ Serviços parados${NC}"
}

# Logs
logs() {
    echo -e "${CYAN}Logs (Ctrl+C para sair):${NC}"
    docker-compose logs -f music-api
}

# Status
status() {
    echo -e "${BLUE}Status dos serviços:${NC}"
    docker-compose ps
}

# Clean
clean() {
    echo -e "${RED}⚠ Remover containers e volumes?${NC}"
    read -p "Confirmar (yes/no): " confirm
    
    if [ "$confirm" = "yes" ]; then
        docker-compose down -v
        echo -e "${GREEN}✓ Limpeza concluída${NC}"
    else
        echo "Cancelado"
    fi
}

# Informações de acesso
show_access() {
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
    echo -e "${GREEN}  Aplicação disponível:${NC}"
    echo -e "${GREEN}  → API: http://localhost:8080/api${NC}"
    echo -e "${GREEN}  → Swagger: http://localhost:8080/api/swagger-ui.html${NC}"
    echo -e "${GREEN}  → Login: http://localhost:8080/api/login${NC}"
    echo -e "${GREEN}  → MinIO Console: http://localhost:9001${NC}"
    echo -e "${GREEN}═══════════════════════════════════════${NC}"
}

# Main
case "$1" in
    build)
        build
        ;;
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        sleep 2
        start
        ;;
    logs)
        logs
        ;;
    status)
        status
        ;;
    clean)
        clean
        ;;
    *)
        show_usage
        exit 1
        ;;
esac
