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

Abra `apps/ios/Yomora.xcodeproj`, escolha um iPhone Simulator e execute o esquema `Yomora`. No simulador, a URL padrão da API é `http://localhost:8080`. Para usar um iPhone físico, siga `apps/ios/Config/README.md`; a URL local do dispositivo fica em `Config/Local.xcconfig`, que não é versionado.

## Comandos

```bash
make backend      # API fora do Docker
make test         # testes backend + PostgreSQL Testcontainers
make ios-test     # XCTest e XCUITest
make openapi      # lint do contrato em container
make docker-down
```

Ao adicionar arquivos ou targets no app, execute `cd apps/ios && xcodegen generate` antes de abrir o projeto ou rodar os testes.

## Configuração

Copie `.env.example` para `.env`. Todas as credenciais incluídas são locais e descartáveis. Defina chaves aleatórias e distintas de pelo menos 32 bytes em `JWT_SECRET` e `EXTERNAL_TOKEN_ENCRYPTION_KEY` fora do ambiente local. `GOOGLE_BOOKS_API_KEY` é opcional durante o desenvolvimento; quando preenchida, eleva a cota do provedor. O fallback Open Library não requer chave. O comando `make backend` carrega automaticamente as variáveis desse arquivo; reinicie a API depois de preencher ou alterar a chave.

### Sign in with Apple e e-mail

O app já contém o entitlement, o botão nativo, nonce, onboarding e gestão dos métodos de acesso. Para habilitar em um ambiente real:

1. Ative **Sign in with Apple** e **Associated Domains** para `app.yomora.ios` no Apple Developer.
2. Gere uma chave privada Apple e mantenha o arquivo `.p8` fora do repositório.
3. Configure `APPLE_SIGN_IN_ENABLED=true`, `APPLE_CLIENT_ID`, `APPLE_TEAM_ID`, `APPLE_KEY_ID` e `APPLE_PRIVATE_KEY` somente no secret manager do ambiente.
4. Publique a API em HTTPS, configure `PUBLIC_API_URL` e faça o domínio definido por `ASSOCIATED_DOMAIN` apontar para ela. O backend serve `/.well-known/apple-app-site-association`.
5. No perfil `prod`, configure `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD` e `MAIL_FROM`. A aplicação falha na inicialização se o transporte de e-mail obrigatório não estiver configurado.

`APPLE_PRIVATE_KEY` aceita o conteúdo PEM com quebras de linha reais ou representadas por `\n`. Nenhuma credencial Apple ou SMTP deve ser colocada em `Local.xcconfig`.

## Dados de demonstração

Depois de `make seed`, use:

- `marina@yomora.local` / `Yomora123!`
- `caio@yomora.local` / `Yomora123!`

O conjunto inclui um livro, biblioteca em andamento, meta, sessão, seguimento e recomendação. O seed é idempotente.

## Testes e CI

O backend possui testes unitários de regras, controller/segurança, arquitetura, agregação de provedores e integração real com PostgreSQL via Testcontainers. O app possui testes de ViewModels, timer, HTTP/Keychain e um fluxo XCUITest. GitHub Actions separa backend, OpenAPI, iOS e segurança; o job iOS usa runner macOS. SpotBugs e FindSecBugs analisam o bytecode Java durante `./gradlew check`, enquanto Semgrep CE analisa Java e Swift, Trivy verifica dependências, segredos e configurações, e zizmor audita os workflows. Dependabot mantém as dependências Gradle e GitHub Actions atualizadas.

## Experiência de leitura

A API mantém uma única sessão ativa por usuário e persiste pausa, tempo decorrido e página atual. O app restaura essa sessão ao abrir ou voltar ao primeiro plano. Durante a leitura, uma Live Activity exibe o estado na Tela Bloqueada e Dynamic Island; o toque retorna ao timer ativo. Em Configurações, o leitor pode ativar lembretes locais por horário e dias da semana. O Yomora deixa de lembrar no dia quando a meta diária já foi concluída.

## Decisões e limitações do MVP

As decisões estruturais estão em `docs/decisions` e `docs/adr`. Sign in with Apple depende da configuração da capability, chave e domínio no Apple Developer. Em desenvolvimento, os links de e-mail ficam em uma caixa local em memória; em produção, o adapter SMTP é obrigatório. Aprovação de seguidores de contas privadas, notificações push remotas e telemetria com consentimento ficam para uma próxima versão.

Próximos passos naturais: APNs, moderação administrativa e telemetria de produto com consentimento.

## Licença

O Yomora é distribuído sob a [licença MIT](LICENSE).
