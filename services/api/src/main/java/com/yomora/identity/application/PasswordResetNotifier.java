package com.yomora.identity.application;

public interface PasswordResetNotifier {
    void send(String email, String rawToken);
}
