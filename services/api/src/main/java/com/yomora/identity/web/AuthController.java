package com.yomora.identity.web;

import com.yomora.identity.application.AuthenticationService;
import com.yomora.identity.application.EmailVerificationService;
import com.yomora.identity.application.ExternalAuthenticationService;
import com.yomora.identity.application.ExternalSignInCommand;
import com.yomora.identity.application.ExternalAccountEvent;
import com.yomora.identity.application.ExternalAccountEventVerifier;
import com.yomora.identity.application.ExternalAccessMethodService;
import com.yomora.identity.application.PasswordResetService;
import com.yomora.identity.application.RegisterCommand;
import com.yomora.identity.application.TokenPair;
import com.yomora.identity.domain.ExternalProvider;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;
    private final EmailVerificationService emailVerificationService;
    private final ExternalAuthenticationService externalAuthenticationService;
    private final ExternalAccountEventVerifier externalAccountEventVerifier;
    private final ExternalAccessMethodService externalAccessMethodService;
    private final CurrentUser currentUser;

    AuthController(
            AuthenticationService authenticationService,
            PasswordResetService passwordResetService,
            EmailVerificationService emailVerificationService,
            ExternalAuthenticationService externalAuthenticationService,
            ExternalAccountEventVerifier externalAccountEventVerifier,
            ExternalAccessMethodService externalAccessMethodService,
            CurrentUser currentUser
    ) {
        this.authenticationService = authenticationService;
        this.passwordResetService = passwordResetService;
        this.emailVerificationService = emailVerificationService;
        this.externalAuthenticationService = externalAuthenticationService;
        this.externalAccountEventVerifier = externalAccountEventVerifier;
        this.externalAccessMethodService = externalAccessMethodService;
        this.currentUser = currentUser;
    }

    @PostMapping("/register")
    ResponseEntity<TokenPair> register(@Valid @RequestBody RegisterRequest request) {
        TokenPair tokens = authenticationService.register(new RegisterCommand(
                request.name(), request.username(), request.email(), request.password()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }

    @PostMapping("/login")
    TokenPair login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.login(request.email(), request.password());
    }

    @PostMapping("/apple")
    ResponseEntity<TokenPair> apple(@Valid @RequestBody AppleSignInRequest request) {
        TokenPair tokens = externalAuthenticationService.authenticate(new ExternalSignInCommand(
                ExternalProvider.APPLE,
                request.identityToken(),
                request.authorizationCode(),
                request.nonce(),
                request.fullName()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }

    @PostMapping("/apple/events")
    ResponseEntity<Void> appleEvents(@Valid @RequestBody ExternalAccountEventRequest request) {
        ExternalAccountEvent event = externalAccountEventVerifier.verify(request.payload());
        if (event.type().revokesAccess()) {
            externalAccessMethodService.handleProviderRevocation(event.provider(), event.subject());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    TokenPair refresh(@Valid @RequestBody RefreshRequest request) {
        return authenticationService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authenticationService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset/request")
    ResponseEntity<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.request(request.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password-reset/confirm")
    ResponseEntity<Void> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        passwordResetService.confirm(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email-verification/request")
    ResponseEntity<Void> requestEmailVerification(Authentication authentication) {
        emailVerificationService.request(currentUser.id(authentication));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/email-verification/confirm")
    ResponseEntity<Void> confirmEmailVerification(@Valid @RequestBody EmailVerificationConfirmRequest request) {
        emailVerificationService.confirm(request.token());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/email-verification/confirm", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> confirmEmailVerificationLink(@RequestParam String token) {
        emailVerificationService.confirm(token);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("""
                        <!doctype html><html lang="pt-BR"><meta charset="utf-8">
                        <meta name="viewport" content="width=device-width,initial-scale=1">
                        <title>E-mail confirmado</title><body><main>
                        <h1>E-mail confirmado</h1><p>Seu endereço foi confirmado com sucesso.</p>
                        <p><a href="yomora://email-verification">Voltar ao Yomora</a></p>
                        </main></body></html>
                        """);
    }

    record RegisterRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(min = 3, max = 40)
            @Pattern(regexp = "^[\\p{L}0-9._-]+$", message = "use apenas letras, números, ponto, hífen ou sublinhado")
            String username,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 8, max = 72) String password
    ) {
    }

    record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }

    record AppleSignInRequest(
            @NotBlank String identityToken,
            @NotBlank String authorizationCode,
            @NotBlank @Size(max = 256) String nonce,
            @Size(max = 120) String fullName
    ) {
    }

    record ExternalAccountEventRequest(@NotBlank String payload) {
    }

    record RefreshRequest(@NotBlank String refreshToken) {
    }

    record PasswordResetRequest(@NotBlank @Email String email) {
    }

    record PasswordResetConfirmRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 8, max = 72) String newPassword
    ) {
    }

    record EmailVerificationConfirmRequest(@NotBlank String token) {
    }
}
