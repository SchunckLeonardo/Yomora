package com.yomora.identity.application;

public interface PasswordResetNotifier {
    void sendPasswordReset(String email, String rawToken);
}
