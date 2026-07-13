# Yomora

> Transformar livros começados em livros terminados.

Yomora é um aplicativo iOS de constância de leitura, biblioteca pessoal e comunidade. Este monorepo contém o cliente SwiftUI, uma API Spring Boot em monólito modular, o contrato OpenAPI e o ambiente Docker local.

## Arquitetura

- `apps/ios`: iOS 17+, SwiftUI, MVVM por feature, Swift Concurrency, SwiftData e Keychain.
- `services/api`: Java 21, Spring Boot, JWT, JPA, Flyway e PostgreSQL.
- `contracts/openapi`: contrato HTTP versionado em `/api/v1`.
- `infrastructure`: PostgreSQL, API e Adminer opcional.
- `docs`: produto, arquitetura, API e ADRs.

O backend é um monólito modular. Os módulos `identity`, `catalog`, `library`, `reading`, `social`, `review`, `moderation` e `notification` mantêm domínio, aplicação, infraestrutura e web próximos. O teste ArchUnit protege o domínio de dependências de framework.

## Requisitos

- JDK 21 (o Gradle Toolchain pode localizá-lo automaticamente)
- Docker Desktop
- Xcode 16 ou superior, com um simulador iOS 17+
- `curl` e `make`

## Primeira execução

```bash
make setup
make docker-up
./scripts/wait-for-api.sh
make seed
```

A API fica em `http://localhost:8080`; Swagger UI em `http://localhost:8080/swagger-ui.html`. Para banco com interface visual, execute `docker compose --profile tools --env-file .env -f infrastructure/docker-compose.yml up -d` e abra o Adminer em `http://localhost:8081`.

Abra `apps/ios/Yomora.xcodeproj`, escolha um iPhone Simulator e execute o esquema `Yomora`. No simulador, a URL padrão da API é `http://localhost:8080`.

## Comandos

```bash
make backend      # API fora do Docker
make test         # testes backend + PostgreSQL Testcontainers
make ios-test     # XCTest e XCUITest
make openapi      # lint do contrato em container
make docker-down
```

## Configuração

Copie `.env.example` para `.env`. Todas as credenciais incluídas são locais e descartáveis. Defina uma chave aleatória de pelo menos 32 bytes em `JWT_SECRET` fora do ambiente local. `GOOGLE_BOOKS_API_KEY` é opcional durante o desenvolvimento; quando preenchida, eleva a cota do provedor. O fallback Open Library não requer chave.

## Dados de demonstração

Depois de `make seed`, use:

- `marina@yomora.local` / `Yomora123!`
- `caio@yomora.local` / `Yomora123!`

O conjunto inclui um livro, biblioteca em andamento, meta, sessão, seguimento e recomendação. O seed é idempotente.

## Testes e CI

O backend possui testes unitários de regras, controller/segurança, arquitetura, agregação de provedores e integração real com PostgreSQL via Testcontainers. O app possui testes de ViewModels, timer, HTTP/Keychain e um fluxo XCUITest. GitHub Actions separa backend, OpenAPI e iOS; o job iOS usa runner macOS.

## Decisões e limitações do MVP

As cinco decisões estruturais estão em `docs/decisions`. Sign in with Apple e Live Activities possuem fronteiras prontas, mas a ativação exige entitlements e credenciais do time Apple. Recuperação de senha usa uma caixa local em memória no profile `local`; produção deve fornecer o adapter de e-mail. Aprovação de seguidores de contas privadas, notificações push e links web universais ficam para a próxima versão.

Próximos passos naturais: adapter de e-mail, APNs, Universal Links, moderação administrativa e telemetria de produto com consentimento.

