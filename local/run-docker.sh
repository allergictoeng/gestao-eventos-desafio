#!/bin/bash

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}=== Iniciando faxina das pastas locais a partir de /local ===${NC}"

# 1. Limpeza do Backend (Target)
if [ -d "../gestao-eventos-backend/target" ]; then
    echo -e "Removendo pasta: ${YELLOW}gestao-eventos-backend/target${NC}"
    rm -rf ../gestao-eventos-backend/target
else
    echo "Pasta target já estava limpa."
fi

# 2. Limpeza do Frontend (node_modules)
if [ -d "../gestao-eventos-front/node_modules" ]; then
    echo -e "Removendo pasta: ${YELLOW}gestao-eventos-front/node_modules${NC}"
    rm -rf ../gestao-eventos-front/node_modules
else
    echo "Pasta node_modules já estava limpa."
fi

echo -e "${GREEN}=== Faxina de arquivos concluída! ===${NC}"

# 3. Limpeza do ambiente Docker antigo (Efeito Docker Compose Down)
echo -e "${YELLOW}=== Derrubando e limpando containers/redes antigos ===${NC}"
docker compose down --remove-orphans

echo -e "${GREEN}=== Ambiente Docker limpo! ===${NC}"
echo -e "${YELLOW}=== Iniciando o Docker Compose (Build Fresh) ===${NC}"

docker compose up --build