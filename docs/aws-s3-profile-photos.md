# Fotos de perfil no Amazon S3

O Yomora mantém o bucket privado. O app nunca recebe credenciais AWS: a API cria uma URL `PUT` assinada por 10 minutos, confirma que o objeto existe e salva somente a chave `avatars/{userId}/{uuid}.ext`. Nas respostas de perfil, a API converte essa chave em uma URL `GET` temporária.

## Bucket

1. Crie um bucket dedicado na mesma região da API.
2. Mantenha **Block Public Access** habilitado em todas as opções.
3. Mantenha Object Ownership em `Bucket owner enforced`; o Yomora não envia ACL pública.
4. Habilite a criptografia padrão do bucket (SSE-S3 ou SSE-KMS).

## Permissões da API

Associe à role da API uma política mínima, trocando `SEU_BUCKET`:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:GetObject", "s3:PutObject"],
      "Resource": "arn:aws:s3:::SEU_BUCKET/avatars/*"
    }
  ]
}
```

Em produção, prefira uma IAM Role da infraestrutura. Para desenvolvimento local, use o arquivo `.env`, que já é ignorado pelo Git, ou um profile da AWS. Nunca preencha credenciais reais em `.env.example`.

## Variáveis

- `AWS_S3_AVATAR_BUCKET`: nome do bucket; vazio desabilita uploads sem impedir a API de iniciar.
- `AWS_REGION`: região do bucket.
- `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` e `AWS_SESSION_TOKEN`: somente quando não houver uma role/profile disponível.
- `AWS_S3_ENDPOINT` e `AWS_S3_PATH_STYLE_ACCESS`: opcionais para um emulador compatível, como LocalStack.
- `YOMORA_ADMIN_USER_IDS`: UUIDs de administradores separados por vírgula.

Os endpoints administrativos ficam sob `/api/v1/admin/moderation` e continuam protegidos pelo JWT e pela lista explícita de administradores.
