# ADR 0006 — Moderação da Comunidade e avatares privados

## Status

Aceita.

## Contexto

A Comunidade precisa permitir denúncia sem bloqueio automático, oferecer revisão administrativa pela API e impedir que uma suspensão social remova acesso à leitura ou à biblioteca. Fotos personalizadas de perfil não podem tornar o bucket público nem expor credenciais AWS ao app.

## Decisão

- Administradores são identificados por UUIDs configurados em `YOMORA_ADMIN_USER_IDS`; não existe administrador implícito.
- Denúncias de perfis e publicações entram como `OPEN` e podem ser marcadas como `REVIEWED`, `DISMISSED` ou `ACTIONED` pelos endpoints `/api/v1/admin/moderation`.
- Suspensões da Comunidade duram exatamente 1, 7 ou 30 dias e podem ser revertidas manualmente. Um filtro central bloqueia publicações, atividades sociais, moderação do usuário e perfis sociais, mas preserva identidade, leitura e biblioteca.
- O bucket Amazon S3 permanece privado. A API assina URLs temporárias `PUT` e `GET`, valida tamanho/tipo e posse da chave, e persiste somente a chave do objeto.
- A cadeia padrão de credenciais da AWS é usada para permitir IAM Roles em produção e credenciais locais sem versionamento durante o desenvolvimento.

## Consequências

O app consegue explicar uma suspensão antes de carregar o feed e continua utilizável para leitura. URLs de avatar expiram e devem ser renovadas quando o perfil é recarregado. Operar a moderação exige configurar explicitamente ao menos um UUID de administrador, e a infraestrutura deve conceder somente `s3:GetObject` e `s3:PutObject` no prefixo `avatars/*`.
