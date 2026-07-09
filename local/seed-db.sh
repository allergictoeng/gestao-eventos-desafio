#!/bin/bash

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}=== Inserindo dados de teste no banco de dados... ===${NC}"

if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

USER=${DB_USER}
DB=${DB_NAME}

docker exec -i gestao-eventos-db psql -U "$USER" -d "$DB" << 'EOF'
DO $$
DECLARE
    i INTEGER := 1;
BEGIN
    FOR i IN 1..40 LOOP
        INSERT INTO events (titulo, descricao, data_hora, local, deleted, created_at)
        VALUES (
            'Evento de Teste #' || i,
            'Descrição detalhada para o evento ' || i,
            NOW() + (i * INTERVAL '1 day'),
            'Local ' || (CASE WHEN i % 2 = 0 THEN 'A' ELSE 'B' END),
            FALSE,          -- deleted
            NOW()           -- created_at
        );
    END LOOP;
END $$;
EOF

if [ $? -eq 0 ]; then
    echo -e "${GREEN}=== 40 Eventos de teste inseridos com sucesso! ===${NC}"
else
    echo -e "\033[0;31m=== Erro ao inserir dados. Verifique os logs acima. ===\033[0m"
fi