package com.yomora.identity.web;

import com.yomora.identity.infrastructure.notification.LocalPasswordResetMailbox;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("local")
@RequestMapping("/api/v1/local/password-reset-mailbox")
class LocalPasswordResetMailboxController {
    private final LocalPasswordResetMailbox mailbox;

    LocalPasswordResetMailboxController(LocalPasswordResetMailbox mailbox) {
        this.mailbox = mailbox;
    }

    @GetMapping("/{email}")
    ResetTokenResponse latest(@PathVariable String email) {
        return new ResetTokenResponse(mailbox.latestFor(email).orElse(null));
    }

    record ResetTokenResponse(String token) {
    }
}
