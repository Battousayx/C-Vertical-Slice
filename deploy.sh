#!/bin/bash

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}"
echo "╔═══════════════════════════════════════╗"
echo "║     Music API - Deploy Script         ║"
echo "╔═══════════════════════════════════════╗"
echo -e "${NC}"

# Função para mostrar uso
show_usage() {
    echo "Uso: $0 [comando]"
    echo ""
    echo "Comandos disponíveis:"
    echo "  build      - Gera o JAR da aplicação"
    echo "  docker     - Constrói a imagem Docker"
    echo "  start      - Inicia todos os serviços"
    echo "  deploy     - Build completo + start (build JAR + docker + start)"
    echo "  logs       - Exibe logs da aplicação"
    echo "  stop       - Para todos os serviços"
    echo "  restart    - Reinicia todos os serviços"
    echo "  clean      - Remove containers, volumes e imagens"
    echo "  status     - Mostra status dos serviços"
    echo ""
    echo "Exemplo: $0 deploy"
}

# Função para build do JAR
build_jar() {
    echo -e "${YELLOW}→ Construindo JAR...${NC}"
    ./mvnw clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        cp target/music-api-0.0.1-SNAPSHOT.jar music-app.jar
        echo -e "${GREEN}✓ JAR criado com sucesso: music-app.jar${NC}"
        ls -lh music-app.jar
    else
        echo -e "${RED}✗ Erro ao criar JAR${NC}"
        exit 1
    fi
}

# Função para build da imagem Docker
build_docker() {
    echo -e "${YELLOW}→ Building Docker image...${NC}"
    docker compose build music-api
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Imagem Docker criada com sucesso${NC}"
    else
        echo -e "${RED}✗ Erro ao criar imagem Docker${NC}"
        exit 1
    fi
}

# Função para iniciar serviços
start_services() {
    echo -e "${YELLOW}→ Iniciando serviços...${NC}"
    docker compose up -d
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Serviços iniciados com sucesso${NC}"
        echo ""
        echo -e "${BLUE}Aguardando inicialização completa...${NC}"
        sleep 10
        
        echo ""
        echo -e "${GREEN}═══════════════════════════════════════${NC}"
        echo -e "${GREEN}  Aplicação disponível em:${NC}"
        echo -e "${GREEN}  → API: http://localhost:8080/api${NC}"
        echo -e "${GREEN}  → Swagger: http://localhost:8080/api/swagger-ui.html${NC}"
        echo -e "${GREEN}  → Login: http://localhost:8080/api/login${NC}"
        echo -e "${GREEN}  → MinIO Console: http://localhost:9001${NC}"
        echo -e "${GREEN}═══════════════════════════════════════${NC}"
    else
        echo -e "${RED}✗ Erro ao iniciar serviços${NC}"
        exit 1
    fi
}

# Função para exibir logs
show_logs() {
    docker compose logs -f music-api
}

# Função para parar serviços
stop_services() {
    echo -e "${YELLOW}→ Parando serviços...${NC}"
    docker compose down
    echo -e "${GREEN}✓ Serviços parados${NC}"
}

# Função para limpar tudo
clean_all() {
    echo -e "${RED}→ Removendo containers, volumes e imagens...${NC}"
    docker compose down -v --rmi local
    echo -e "${GREEN}✓ Limpeza concluída${NC}"
}

# Função para mostrar status
show_status() {
    echo -e "${BLUE}Status dos serviços:${NC}"
    docker compose ps
    echo ""
    echo -e "${BLUE}Uso de recursos:${NC}"
    docker compose stats --no-stream
}

# Processar comando
case "$1" in
    build)
        build_jar
        ;;
    docker)
        build_docker
        ;;
    start)
        start_services
        ;;
    deploy)
        build_jar
        build_docker
        start_services
        ;;
    logs)
        show_logs
        ;;
    stop)
        stop_services
        ;;
    restart)
        stop_services
        sleep 2
        start_services
        ;;
    clean)
        clean_all
        ;;
    status)
        show_status
        ;;
    *)
        show_usage
        exit 1
        ;;
esac
