# 📅 Gestão de Eventos - Desafio Técnico

Este repositório contém uma solução full-stack para o sistema de Gestão de Eventos, composta por uma API em Spring Boot (Java 21) e uma interface em Angular (v18+).

A arquitetura está totalmente conteinerizada com Docker para facilitar o desenvolvimento, limpeza e execução do ecossistema.

---

## 🏗️ Estrutura do Repositório

gestao-eventos-desafio/

├── gestao-eventos-backend/ # API Spring Boot (Java 21)

├── gestao-eventos-front/ # SPA Angular com builder moderno

└── local/ # Docker Compose, .env e Scripts de Automação

---

## 🛠️ Como Executar o Projeto com Docker (Recomendado)

O projeto possui um script de automação localizado dentro da pasta `/local` que executa uma limpeza completa nas pastas de compilação locais (target e node_modules), limpa o estado anterior do Docker e sobe o ambiente 100% atualizado.

1. Entrar na pasta de infraestrutura:
   cd local

2. Dar permissão de execução ao script (Apenas na primeira vez):
   chmod +x run-docker.sh

3. Executar o ambiente automatizado:
   ./run-docker.sh

Nota: Este script limpa o cache físico da sua máquina, derruba os containers órfãos antigos e ergue a arquitetura nova do zero.

---

## 🐳 Comandos Manuais do Docker Compose

Se preferir gerenciar os containers manualmente em segundo plano (detached), execute os comandos abaixo diretamente a partir da pasta `/local`:

### Subir o ambiente inteiro em background reconstruindo as imagens

docker compose up -d --build

### Derrubar a arquitetura limpando redes e containers órfãos

docker compose down --remove-orphans

---

## 🗄️ Inserindo Dados de Teste (Seed)

Para não começar com o banco de dados vazio, há um script utilitário em `/local` que adiciona 40 eventos fictícios populados automaticamente via bloco anônimo PL/pgSQL, respeitando as mesmas credenciais do teu arquivo `.env`.

Com os containers já ativos, execute de dentro da pasta `/local`:

### Se for a primeira vez, dê a permissão

chmod +x seed-db.sh

### Execute o populador

./seed-db.sh

---

## 🌐 Endereços de Acesso Local

Após subir os containers com sucesso, as aplicações estarão disponíveis nos seguintes endereços:

- Frontend (Interface Angular): http://localhost (Porta padrão 80)
- Backend (API Spring Boot): http://localhost:8080/api/events (Porta 8080)
- Swagger (API Contract): http://localhost:8080/swagger-ui.html (Porta 8080)
- Banco de Dados (PostgreSQL): localhost:5432
- Todos os dados de acesso, estão dentro do arquivo `.env` na pasta `/local`

---

## 🧪 Notas Importantes de Configuração

- Segurança (CORS): O backend possui uma configuração explícita para aceitar conexões vindas da origem http://localhost, evitando bloqueios de requisições assíncronas do navegador ao salvar ou listar eventos.
- Build Otimizado: Os Dockerfiles utilizam Multi-stage builds, garantindo que as imagens de runtime finais (Nginx para o front e Alpine JRE para o back).
- O ambiente foi projetado utilizando o princípio do menor privilégio através de duas redes isoladas:
* **`db_net` (Privada do Banco):** Conecta exclusivamente o banco de dados (`db`) e a API (`backend`). O frontend está fisicamente impossibilitado de enxergar ou pingar o PostgreSQL, blindando a porta 5432 de varreduras maliciosas.
* **`front_net` (Camada de Aplicação):** Conecta a API (`backend`) e o servidor Nginx do `frontend` para o tráfego das requisições HTTP na porta 8080.
* **O papel da API:** O container do Spring Boot atua como um dispositivo *Dual-Homed*, possuindo duas placas de rede virtuais para intermediar o fluxo de forma segura entre o cliente e o banco de dados.