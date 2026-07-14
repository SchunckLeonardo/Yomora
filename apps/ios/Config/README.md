# Configuração local do iOS

Crie a configuração local antes de gerar o projeto:

```bash
cp Config/Info.plist.example Config/Info.plist
```

Depois, altere somente `API_BASE_URL` em `Config/Info.plist` e execute:

```bash
xcodegen generate
```

`Config/Info.plist` é local e ignorado pelo Git. O arquivo de exemplo não contém credenciais e é usado pelo CI.
