package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.IdentitySecretCipher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;
import jakarta.annotation.PostConstruct;

@Component
class AesGcmIdentitySecretCipher implements IdentitySecretCipher {
    private static final int IV_LENGTH = 12;
    private static final int TAG_BITS = 128;
    private final SecretKeySpec key;
    private final int secretLength;
    private final SecureRandom secureRandom = new SecureRandom();

    AesGcmIdentitySecretCipher(
            @Value("${yomora.external-token-encryption-key:local-development-external-token-key}") String secret
    ) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretLength = bytes.length;
        this.key = new SecretKeySpec(Arrays.copyOf(bytes, 32), "AES");
    }

    @PostConstruct
    void validateConfiguration() {
        if (secretLength < 32) {
            throw new IllegalStateException("EXTERNAL_TOKEN_ENCRYPTION_KEY deve possuir pelo menos 32 bytes");
        }
    }

    @Override
    public String encrypt(String value) {
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(ByteBuffer.allocate(iv.length + encrypted.length).put(iv).put(encrypted).array());
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Não foi possível criptografar o token externo", exception);
        }
    }

    @Override
    public String decrypt(String value) {
        byte[] payload = Base64.getUrlDecoder().decode(value);
        if (payload.length <= IV_LENGTH) {
            throw new IllegalArgumentException("Token externo criptografado inválido");
        }
        byte[] iv = new byte[IV_LENGTH];
        byte[] encrypted = new byte[payload.length - IV_LENGTH];
        System.arraycopy(payload, 0, iv, 0, IV_LENGTH);
        System.arraycopy(payload, IV_LENGTH, encrypted, 0, encrypted.length);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException exception) {
            throw new IllegalArgumentException("Token externo criptografado inválido", exception);
        }
    }

}
