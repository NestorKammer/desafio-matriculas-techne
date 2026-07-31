# Gestão de Matrículas Acadêmicas

Solução do desafio técnico Pleno (Tribe Lyceum / Techne): API Spring Boot + frontend Angular + SQL Server, com regras de matrícula, controle de vagas, testes e Docker Compose.

## Stack

| Camada | Tecnologia |
|--------|------------|
| Backend | Java 21, Spring Boot 3.3, Spring Web, Spring Data JPA, Bean Validation |
| Persistência | SQL Server 2022, Liquibase, Hibernate (`ddl-auto=validate`) |
| API docs | springdoc-openapi (Swagger UI) |
| Frontend | Angular 19 (standalone), Reactive Forms, HttpClient |
| Testes | JUnit 5, MockMvc, H2 (profile `test`) |
| Infra | Docker Compose (SQL Server + backend + frontend/nginx) |

## Pré-requisitos

- Docker + Docker Compose **ou**
- Java 21 + Maven 3.9+ e Node 20+ (modo desenvolvimento local)

## Como executar (recomendado: stack completa)

Na raiz do repositório:

```bash
cp .env.example .env   # se ainda não existir
./bin/stack-up.sh
# equivalente:
# docker compose --env-file .env up -d --build
```

Serviços publicados:

| Serviço | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| SQL Server | `localhost:1433` / database `matriculas_db` |

O nginx do frontend faz proxy de `/api` (e Swagger) para o backend ? dá para usar a UI em `:4200` sem CORS.

Credenciais padrão (`.env`):

- usuário: `sa`
- senha: `Matriculas@2026`

### Só o banco

```bash
./bin/db-up.sh
```

### Desenvolvimento local (sem containers de app)

```bash
# 1) banco
./bin/db-up.sh

# 2) API
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=docker

# 3) UI (outro terminal)
cd frontend
npm install
npm start
# http://localhost:4200  (proxy.conf.json ? :8080)
```

## Como executar os testes

```bash
cd backend
mvn test
```

Suíte atual (~18 testes):

- `MatriculaRegrasTest` ? regras de domínio (vagas, status, turma fechada, aluno inativo)
- `AlunoApiTest` ? CRUD, validação 400, duplicidade, 404
- `MatriculaApiTest` ? fluxo PENDENTE ? confirmar/cancelar, sem vaga, duplicada, consultas

Profile `test`: H2 + Liquibase (mesmo changelog da aplicação).

## Swagger / OpenAPI

Com o backend no ar:

- UI: http://localhost:8080/swagger-ui/index.html
- Também via frontend: http://localhost:4200/swagger-ui/index.html

Endpoints principais:

- `/api/alunos`, `/api/cursos`, `/api/disciplinas`, `/api/turmas` ? CRUD
- `/api/matriculas` ? criar (PENDENTE), `/{id}/confirmar`, `/{id}/cancelar`
- `GET /api/matriculas?alunoId=` ou `?turmaId=`

## Arquitetura (camadas)

```
controller (api)  ? DTOs + Bean Validation
service           ? regras + @Transactional
domain            ? entidades, enums, comportamento de negócio
repository        ? Spring Data JPA
shared            ? ErroResposta + @RestControllerAdvice
```

Organização por feature: `aluno`, `curso`, `disciplina`, `turma`, `matricula`.

Schema versionado em:

`backend/src/main/resources/db/changelog/`

## Principais decisões técnicas

1. **Matrícula inicia em `PENDENTE`** e **não consome vaga**. O consumo ocorre só em `confirmar`, alinhado ao enunciado (status + vagas).
2. **Liquibase dono do schema**; Hibernate em `validate` ? evita drift entre migração e entidades.
3. **DTOs na borda HTTP**; entidades não são expostas na API.
4. **Erros padronizados** (`ErroResposta`): 400 validação, 404 recurso, 409 regra/negócio/integridade.
5. **Frontend** separado, consumindo a API; em Compose, nginx unifica origem.
6. **SQL Server no Compose** atende o requisito de ambiente reproduzível (sem depender de host externo).

## Como a regra de vagas foi protegida

Mecanismos combinados:

1. Domínio (`Turma.consumirVaga` / `liberarVaga`) valida turma `ABERTA` e `vagasOcupadas < vagasTotais`.
2. Serviço de confirmação/cancelamento usa **`@Transactional`** + **`PESSIMISTIC_WRITE`** na turma (`findByIdForUpdate`), serializando concorrência na mesma turma.
3. Após o lock, a matrícula é **relida** antes de confirmar (evita double-confirm).
4. Constraint **UNIQUE `(aluno_id, turma_id)`** no banco.
5. Check constraint de vagas no Liquibase (`vagas_ocupadas <= vagas_totais`).
6. Campo **`@Version`** na turma como reforço otimista.

Cancelar `CONFIRMADA` libera vaga; cancelar `PENDENTE` só muda status.

## Como as regras críticas foram testadas

| Cenário | Onde |
|---------|------|
| PENDENTE não consome vaga | domínio + API |
| Confirmar consome 1 vaga | domínio + API |
| Cancelar CONFIRMADA libera vaga | domínio + API |
| Turma FECHADA bloqueia matrícula | domínio + API |
| Sem vaga na confirmação | domínio + API |
| Duplicidade aluno+turma | API (409) |
| Consulta por aluno / turma | API |
| Validação de entrada / 404 | API |

## Fluxo sugerido na UI

1. Cadastrar **Curso** ? **Disciplina** ? **Turma** (ABERTA, com vagas)
2. Cadastrar **Aluno**
3. Em **Matrículas**: criar (PENDENTE) ? **Confirmar** (consome vaga) ou **Cancelar**

## Dados iniciais (seed)

No profile Docker, o Liquibase carrega **50 alunos** de demonstração (`context=seed`):

- RA: `SEED001` ? `SEED050`
- E-mail: `aluno01@matriculas.local` ? `aluno50@matriculas.local`

Arquivos: `backend/src/main/resources/db/changelog/changes/002-seed-alunos.xml` e `data/alunos-seed.csv`.  
Nos testes automatizados o seed **não** é aplicado (`contexts: test`).

## Limitações conhecidas

- Sem autenticação/autorização (fora do escopo do desafio).
- Listagens sem paginação/filtros avançados (podem ser adicionados como diferencial).
- Testes de concorrência pesada (duas threads na última vaga) não estão na suíte atual; a proteção é por lock pessimista no serviço.
- Exclusão de entidades com FKs pode falhar por integridade (comportamento esperado; resposta 409).
- Build Docker do backend/frontend é mais lento na primeira execução (download Maven/npm).

## Uso de IA

Ferramenta: **Cursor (Composer / agente de código)**.

| Parte | Uso de IA | Revisão manual |
|-------|-----------|----------------|
| Plano (`docs/plano-implementacao.html`) | Rascunho a partir do PDF | Ajuste de escopo, SQL Server ? Docker, Liquibase |
| Docker Compose / Liquibase / entities | Geração assistida | Validação JDBC, checksums, mapeamento `datetime2` × `LocalDateTime` |
| Regras de matrícula / lock | Proposta + implementação | Conferência do fluxo PENDENTE?CONFIRMAR e unique |
| API (DTOs, controllers, errors, Swagger) | Geração assistida | Smoke test HTTP e contratos de status |
| Testes domínio + MockMvc | Geração assistida | Execução `mvn test`, rename `*IT` ? `*ApiTest` |
| Frontend Angular | Scaffold + telas | Build `ng build`, proxy/nginx |
| README | Redação assistida | Checagem contra checklist do PDF |

### Trechos mais críticos (explicar na entrevista)

1. `Turma.consumirVaga` / `liberarVaga`
2. `Matricula.criarPendente` / `confirmar` / `cancelar`
3. `MatriculaService.confirmar` (lock pessimista + relê)
4. `001-create-schema.xml` (unique + checks de status/vagas)
5. `ApiExceptionHandler` (contrato de erro)

## Estrutura do repositório

```
desafio/
??? backend/                 # Spring Boot
??? frontend/                # Angular 19
??? docker/sqlserver/        # script de criação do database
??? bin/db-up.sh             # sobe só o SQL Server
??? bin/stack-up.sh          # sobe stack completa
??? docker-compose.yml
??? docs/                    # plano e guias auxiliares
??? DESAFI_2.pdf
```

## Comandos úteis

```bash
docker compose --env-file .env ps
docker compose --env-file .env logs -f backend
docker compose --env-file .env down
# apagar volume do banco (cuidado: perde dados):
# docker compose --env-file .env down -v
```
