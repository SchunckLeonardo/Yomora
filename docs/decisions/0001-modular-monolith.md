# ADR 0001: monólito modular

Status: aceito.

Escolhemos uma aplicação Spring Boot implantável para reduzir operação e manter transações simples no MVP. Limites por domínio e ArchUnit conservam a possibilidade de extração futura. Microsserviços, broker e consistência distribuída não têm benefício proporcional nesta fase.

