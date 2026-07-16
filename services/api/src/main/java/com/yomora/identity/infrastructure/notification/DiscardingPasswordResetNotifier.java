package com.yomora.identity.infrastructure.notification;

import com.yomora.identity.application.PasswordResetNotifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!local & !prod")
class DiscardingPasswordResetNotifier implements PasswordResetNotifier {
    @Override
    public void sendPasswordReset(String email, String rawToken) {
        // Intencional: produção deve substituir este adapter por um provedor de e-mail.
    }
}
