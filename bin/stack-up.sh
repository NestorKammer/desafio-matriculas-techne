#!/usr/bin/env bash
# Sobe o stack completo: SQL Server + backend + frontend
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [[ ! -f .env ]]; then
  cp .env.example .env
  echo "Arquivo .env criado a partir de .env.example"
fi

echo ">> Build e start (sqlserver + backend + frontend)..."
docker compose --env-file .env up -d --build

echo ">> Aguardando backend healthy..."
for i in $(seq 1 60); do
  status="$(docker inspect -f '{{.State.Health.Status}}' matriculas-backend 2>/dev/null || echo starting)"
  if [[ "$status" == "healthy" ]]; then
    break
  fi
  echo "   backend=$status ($i/60)"
  sleep 5
done

echo ">> Stack pronto:"
echo "   Frontend: http://localhost:${FRONTEND_PORT:-4200}"
echo "   Backend:  http://localhost:${BACKEND_PORT:-8080}"
echo "   Swagger:  http://localhost:${BACKEND_PORT:-8080}/swagger-ui/index.html"
echo "   (via nginx) http://localhost:${FRONTEND_PORT:-4200}/swagger-ui/index.html"
docker compose --env-file .env ps
