package com.yomora.identity.application;

import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import com.yomora.identity.web.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProfilePhotoService {
    private static final Duration SIGNED_URL_DURATION = Duration.ofMinutes(10);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final UserRepository users;
    private final ProfilePhotoStorage storage;
    private final Clock clock;

    public ProfilePhotoService(UserRepository users, ProfilePhotoStorage storage, Clock clock) {
        this.users = users;
        this.storage = storage;
        this.clock = clock;
    }

    public ProfilePhotoUpload requestUpload(UUID userId, String contentType, long contentLength) {
        requireUser(userId);
        String normalizedContentType = contentType == null ? "" : contentType.trim().toLowerCase();
        if (!CONTENT_TYPES.contains(normalizedContentType)) {
            throw new IllegalArgumentException("Formato de imagem não suportado");
        }
        if (contentLength < 1 || contentLength > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("A imagem deve possuir no máximo 5 MB");
        }
        Instant expiresAt = clock.instant().plus(SIGNED_URL_DURATION);
        ProfilePhotoUpload upload = storage.createUpload(userId, normalizedContentType, contentLength, expiresAt);
        String requiredPrefix = objectPrefix(userId);
        if (!upload.objectKey().startsWith(requiredPrefix)
                || !upload.objectKey().endsWith("." + EXTENSIONS.get(normalizedContentType))) {
            throw new IllegalStateException("O armazenamento retornou uma chave de avatar inválida");
        }
        return upload;
    }

    @Transactional
    public User completeUpload(UUID userId, String objectKey) {
        User current = requireUser(userId);
        if (objectKey == null || !objectKey.startsWith(objectPrefix(userId))) {
            throw new IllegalArgumentException("A imagem não pertence ao usuário autenticado");
        }
        if (!storage.exists(objectKey)) {
            throw new IllegalArgumentException("A imagem ainda não foi enviada");
        }
        return users.save(new User(
                current.id(), current.name(), current.username(), current.email(), current.passwordHash(),
                current.bio(), objectKey, current.publicProfile(), current.createdAt(), clock.instant()
        ));
    }

    public String readUrl(String storedValue) {
        if (storedValue == null || storedValue.isBlank()) {
            return null;
        }
        if (storedValue.startsWith("http://") || storedValue.startsWith("https://")) {
            return storedValue;
        }
        if (!storedValue.startsWith("avatars/")) {
            return null;
        }
        return storage.createReadUrl(storedValue, clock.instant().plus(SIGNED_URL_DURATION));
    }

    private User requireUser(UUID userId) {
        return users.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private String objectPrefix(UUID userId) {
        return "avatars/" + userId + "/";
    }
}
