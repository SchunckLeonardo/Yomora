# ADR 0005: access JWT e refresh token rotativo

Status: aceito.

Access JWT de curta duração mantém a API stateless. Refresh tokens opacos, aleatórios, persistidos como hash e rotacionados a cada uso permitem revogação e logout. O app armazena o par no Keychain. Sign in with Apple entrará por uma porta de identidade externa sem alterar o modelo de sessão.

