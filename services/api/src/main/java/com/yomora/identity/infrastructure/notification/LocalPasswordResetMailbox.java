package com.yomora.identity.infrastructure.notification;

import com.yomora.identity.application.PasswordResetNotifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("local")
public class LocalPasswordResetMailbox implements PasswordResetNotifier {
    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    @Override
    public void send(String email, String rawToken) {
        tokens.put(email.toLowerCase(Locale.ROOT), rawToken);
    }

    public Optional<String> latestFor(String email) {
        return Optional.ofNullable(tokens.get(email.toLowerCase(Locale.ROOT)));
    }
}
