# Configuração local do iOS

O projeto usa `App.xcconfig`, com `http://localhost:8080` como padrão seguro para o simulador.
Para testar em um iPhone físico, crie a configuração local:

```bash
cp Config/Local.xcconfig.example Config/Local.xcconfig
```

Depois, altere somente `API_BASE_URL` em `Config/Local.xcconfig` e execute:

```bash
xcodegen generate
```

`Config/Local.xcconfig` é local e ignorado pelo Git. O plist versionado contém apenas referências a configurações de build.
