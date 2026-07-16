package com.yomora.identity.application;

public interface EmailVerificationNotifier {
    void send(String email, String verificationLink);
}
