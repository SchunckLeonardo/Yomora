# Configuração local do iOS

O projeto usa `App.xcconfig`, com `http://localhost:8080` como padrão seguro para o simulador.
Para testar em um iPhone físico, crie a configuração local:

```bash
cp Config/Local.xcconfig.example Config/Local.xcconfig
```

Depois, altere `API_BASE_URL` em `Config/Local.xcconfig`. Se o ambiente usar outro domínio HTTPS para links universais, configure também `ASSOCIATED_DOMAIN`. Execute:

```bash
xcodegen generate
```

`Config/Local.xcconfig` é local e ignorado pelo Git. O plist versionado contém apenas referências a configurações de build.
Credenciais Apple e SMTP pertencem ao backend/secret manager e nunca devem ser adicionadas a esse arquivo.
