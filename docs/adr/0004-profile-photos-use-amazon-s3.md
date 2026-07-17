# Fotos de perfil usam Amazon S3

As Fotos de perfil são armazenadas no Amazon S3 da AWS. Escolhemos um provedor explícito em vez de uma configuração genérica compatível com S3 para ter integração operacional clara; bucket, região e credenciais permanecem em variáveis de ambiente e nunca são versionados.
