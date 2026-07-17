package com.yomora.moderation.application;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminAccessPolicyTest {
    @Test
    void allowsOnlyExplicitlyConfiguredAdministrators() {
        UUID adminId = UUID.randomUUID();
        AdminAccessPolicy policy = new AdminAccessPolicy(Set.of(adminId));

        assertThatCode(() -> policy.requireAdmin(adminId)).doesNotThrowAnyException();
        assertThatThrownBy(() -> policy.requireAdmin(UUID.randomUUID()))
                .isInstanceOf(AccessDeniedException.class);
    }
}
