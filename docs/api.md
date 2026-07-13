# API

Base local: `http://localhost:8080/api/v1`. O contrato fonte está em `contracts/openapi/yomora-api.yaml`; a documentação gerada pela aplicação fica em `/swagger-ui.html`.

## Autenticação

Envie `Authorization: Bearer <accessToken>`. Ao receber 401, faça uma única tentativa de rotação em `POST /auth/refresh`, persista o novo par e repita a requisição original. Logout revoga o refresh token. Solicitação de recuperação sempre retorna 202, exista ou não a conta.

No profile `local`, a caixa de recuperação pode ser consultada em `GET /api/v1/local/password-reset-mailbox/{email}`; esse endpoint exige autenticação e não existe nos demais ambientes.

## Paginação e erros

Feeds aceitam cursor e limite; o servidor restringe o tamanho. Erros seguem RFC 9457 Problem Details e podem trazer `errors` por campo.

## Compartilhamento

O cliente forma links `https://yomora.app/{resource}/{id}`. O domínio pode ser trocado por configuração até Universal Links entrarem em produção.

