package com.yomora.identity.infrastructure.notification;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SmtpIdentityNotifierTest {
    @Test
    void sendsTheEmailVerificationLinkFromTheConfiguredAddress() {
        JavaMailSender sender = mock(JavaMailSender.class);
        SmtpIdentityNotifier notifier = new SmtpIdentityNotifier(
                sender, "Yomora <no-reply@yomora.app>", "yomora://password-reset?token="
        );

        notifier.send("reader@example.com", "https://api.yomora.app/api/v1/auth/email-verification/confirm?token=abc");

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(message.capture());
        assertThat(message.getValue().getFrom()).isEqualTo("Yomora <no-reply@yomora.app>");
        assertThat(message.getValue().getTo()).containsExactly("reader@example.com");
        assertThat(message.getValue().getSubject()).contains("Confirme");
        assertThat(message.getValue().getText()).contains("https://api.yomora.app/api/v1/auth/email-verification/confirm?token=abc");
    }

    @Test
    void sendsPasswordResetAsADeepLink() {
        JavaMailSender sender = mock(JavaMailSender.class);
        SmtpIdentityNotifier notifier = new SmtpIdentityNotifier(
                sender, "no-reply@yomora.app", "yomora://password-reset?token="
        );

        notifier.sendPasswordReset("reader@example.com", "secret-token");

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(message.capture());
        assertThat(message.getValue().getSubject()).contains("senha");
        assertThat(message.getValue().getText()).contains("yomora://password-reset?token=secret-token");
    }
}
