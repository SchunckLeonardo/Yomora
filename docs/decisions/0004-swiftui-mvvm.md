# ADR 0004: SwiftUI e MVVM por feature

Status: aceito.

SwiftUI atende iOS 17, acessibilidade e previews com pouco estado incidental. ViewModels `@MainActor` expõem estados determinísticos e dependem de protocolos, permitindo testes rápidos. UIKit fica restrito a recursos sem equivalente adequado.

