package com.yomora.identity.infrastructure.notification;

import com.yomora.identity.application.EmailVerificationNotifier;
import com.yomora.identity.application.PasswordResetNotifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
@Profile("prod")
class SmtpIdentityNotifier implements EmailVerificationNotifier, PasswordResetNotifier {
    private final JavaMailSender sender;
    private final String from;
    private final String passwordResetDeepLink;

    SmtpIdentityNotifier(
            JavaMailSender sender,
            @Value("${yomora.mail.from}") String from,
            @Value("${yomora.mail.password-reset-deep-link:yomora://password-reset?token=}")
            String passwordResetDeepLink
    ) {
        this.sender = sender;
        this.from = from;
        this.passwordResetDeepLink = passwordResetDeepLink;
    }

    @PostConstruct
    void validateConfiguration() {
        require(from, "MAIL_FROM");
    }

    @Override
    public void send(String email, String verificationLink) {
        send(email, "Confirme seu e-mail no Yomora", String.join(System.lineSeparator(),
                "Olá,", "", "Confirme seu e-mail acessando o link abaixo:", "", verificationLink, "",
                "O link expira em 24 horas e só pode ser utilizado uma vez."
        ));
    }

    @Override
    public void sendPasswordReset(String email, String rawToken) {
        send(email, "Redefina sua senha no Yomora", String.join(System.lineSeparator(),
                "Olá,", "", "Acesse o link abaixo para redefinir sua senha:", "",
                passwordResetDeepLink + rawToken, "",
                "O link expira em 30 minutos e só pode ser utilizado uma vez."
        ));
    }

    private void send(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        sender.send(message);
    }

    private String require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " é obrigatório no perfil prod");
        }
        return value;
    }
}
