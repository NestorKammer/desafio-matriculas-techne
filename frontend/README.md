# Frontend ? Matriculas Academicas

Angular 19 (standalone) consumindo a API Spring Boot.

## Execucao com Docker Compose (stack completa)

```bash
./bin/stack-up.sh
# ou: docker compose --env-file .env up -d --build
```

- Frontend: http://localhost:4200
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html

O nginx do frontend faz proxy de `/api` para o backend.

## Execucao local (dev)

1. Backend + SQL Server Docker em execucao (`localhost:8080`)
2. Neste diretorio:

```bash
npm start
```

Abra http://localhost:4200  
O proxy (`proxy.conf.json`) encaminha `/api` para `http://localhost:8080`.

## Telas

- Matriculas (criar PENDENTE, confirmar, cancelar, consultar)
- Alunos, Cursos, Disciplinas, Turmas (CRUD)

Erros da API sao exibidos via interceptor HTTP.
