#!/usr/bin/env bash
# Sobe o SQL Server da aplicação e aguarda o database matriculas_db.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [[ ! -f .env ]]; then
  cp .env.example .env
  echo "Arquivo .env criado a partir de .env.example"
fi

echo ">> Subindo SQL Server (Docker Compose)..."
docker compose --env-file .env up -d sqlserver

echo ">> Aguardando healthcheck..."
for i in $(seq 1 40); do
  status="$(docker inspect -f '{{.State.Health.Status}}' matriculas-sqlserver 2>/dev/null || echo starting)"
  if [[ "$status" == "healthy" ]]; then
    break
  fi
  echo "   status=$status ($i/40)"
  sleep 3
done

echo ">> Criando database (sqlserver-init)..."
docker compose --env-file .env run --rm sqlserver-init

echo ">> Pronto. Connection string:"
echo "   jdbc:sqlserver://localhost:${MSSQL_PORT:-1433};databaseName=${MSSQL_DB:-matriculas_db};encrypt=false;trustServerCertificate=true"
echo "   user=sa  password=(ver .env)"
