# Modelar identidades externas independentemente do provedor

O perfil e os dados de leitura continuam pertencendo à Conta Yomora, enquanto cada identidade afirmada por terceiros é armazenada separadamente com seu provedor e identificador estável. A primeira integração será Apple, mas o modelo não terá campos específicos desse provedor, permitindo incluir Google futuramente sem remodelar usuários nem alterar o significado da Conta Yomora. Essa pequena generalização agora evita acoplamento permanente sem ampliar o escopo da implementação atual.
