package com.yomora.identity.infrastructure.notification;

import com.yomora.identity.application.EmailVerificationNotifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!prod")
public class LocalEmailVerificationMailbox implements EmailVerificationNotifier {
    private final Map<String, String> links = new ConcurrentHashMap<>();

    @Override
    public void send(String email, String verificationLink) {
        links.put(email.toLowerCase(Locale.ROOT), verificationLink);
    }

    public Optional<String> latestFor(String email) {
        return Optional.ofNullable(links.get(email.toLowerCase(Locale.ROOT)));
    }
}
