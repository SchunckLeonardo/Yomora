package com.yomora.identity.web;

import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import com.yomora.identity.application.UpdateProfileCommand;
import com.yomora.identity.application.UserProfileService;
import com.yomora.identity.application.ProfileMetrics;
import com.yomora.identity.application.ProfileMetricsService;
import com.yomora.identity.application.ExternalAccessMethodService;
import com.yomora.identity.application.ExternalSignInCommand;
import com.yomora.identity.domain.ExternalProvider;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/users")
class UserController {
    private final UserRepository userRepository;
    private final UserProfileService profileService;
    private final ProfileMetricsService metricsService;
    private final CurrentUser currentUser;
    private final ExternalAccessMethodService accessMethods;

    UserController(UserRepository userRepository, UserProfileService profileService,
                   ProfileMetricsService metricsService, CurrentUser currentUser,
                   ExternalAccessMethodService accessMethods) {
        this.userRepository = userRepository;
        this.profileService = profileService;
        this.metricsService = metricsService;
        this.currentUser = currentUser;
        this.accessMethods = accessMethods;
    }

    @GetMapping("/me")
    UserResponse me(Authentication authentication) {
        User user = userRepository.findById(currentUser.id(authentication))
                .orElseThrow(() -> new UserNotFoundException(currentUser.id(authentication)));
        return UserResponse.from(user, metricsService.forUser(user.id()));
    }

    @PatchMapping("/me")
    UserResponse update(@Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
        User user = profileService.update(currentUser.id(authentication), new UpdateProfileCommand(
                request.name(), request.username(), request.bio(), request.avatarUrl(), request.publicProfile()
        ));
        return UserResponse.from(user, metricsService.forUser(user.id()));
    }

    @DeleteMapping("/me")
    ResponseEntity<Void> delete(Authentication authentication) {
        accessMethods.deleteAccount(currentUser.id(authentication));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/access-methods")
    ExternalAccessMethodService.AccessMethods accessMethods(Authentication authentication) {
        return accessMethods.methods(currentUser.id(authentication));
    }

    @PostMapping("/me/access-methods/{provider}")
    ResponseEntity<Void> linkAccessMethod(
            @PathVariable String provider,
            @Valid @RequestBody ExternalCredentialRequest request,
            Authentication authentication
    ) {
        accessMethods.link(currentUser.id(authentication), request.command(provider(provider)));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/access-methods/{provider}")
    ResponseEntity<Void> unlinkAccessMethod(
            @PathVariable String provider,
            @Valid @RequestBody ExternalCredentialRequest request,
            Authentication authentication
    ) {
        ExternalProvider externalProvider = provider(provider);
        accessMethods.unlink(
                currentUser.id(authentication), externalProvider, request.command(externalProvider)
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/password")
    ResponseEntity<Void> addPassword(
            @Valid @RequestBody AddPasswordRequest request,
            Authentication authentication
    ) {
        accessMethods.addPassword(
                currentUser.id(authentication), request.newPassword(), request.apple().command(ExternalProvider.APPLE)
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    PublicUserResponse profile(@PathVariable UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return PublicUserResponse.from(user, metricsService.forUser(user.id()));
    }

    record UpdateProfileRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(min = 3, max = 40)
            @Pattern(regexp = "^[\\p{L}0-9._-]+$") String username,
            @Size(max = 500) String bio,
            @Size(max = 500) String avatarUrl,
            boolean publicProfile
    ) {
    }

    record ExternalCredentialRequest(
            @NotBlank String identityToken,
            @NotBlank String authorizationCode,
            @NotBlank @Size(max = 256) String nonce
    ) {
        ExternalSignInCommand command(ExternalProvider provider) {
            return new ExternalSignInCommand(provider, identityToken, authorizationCode, nonce, null);
        }
    }

    record AddPasswordRequest(
            @NotBlank @Size(min = 8, max = 72) String newPassword,
            @NotNull @Valid ExternalCredentialRequest apple
    ) {
    }

    private ExternalProvider provider(String value) {
        return ExternalProvider.valueOf(value.toUpperCase(Locale.ROOT));
    }

    record PublicUserResponse(
            UUID id, String name, String username, String bio, String avatarUrl, boolean publicProfile,
            long followers, long following, long finishedBooks, long totalReadingMinutes, int currentStreak,
            Instant createdAt
    ) {
        static PublicUserResponse from(User user, ProfileMetrics metrics) {
            return new PublicUserResponse(user.id(), user.name(), user.username(), user.bio(), user.avatarUrl(),
                    user.publicProfile(), metrics.followers(), metrics.following(), metrics.finishedBooks(),
                    metrics.totalReadingMinutes(), metrics.currentStreak(), user.createdAt());
        }
    }

    record UserResponse(
            UUID id,
            String name,
            String username,
            String email,
            boolean emailVerified,
            boolean profileComplete,
            String bio,
            String avatarUrl,
            boolean publicProfile,
            long followers,
            long following,
            long finishedBooks,
            long totalReadingMinutes,
            int currentStreak,
            Instant createdAt
    ) {
        static UserResponse from(User user, ProfileMetrics metrics) {
            return new UserResponse(
                    user.id(), user.name(), user.username(), user.email(), user.emailVerified(), user.profileComplete(),
                    user.bio(), user.avatarUrl(),
                    user.publicProfile(), metrics.followers(), metrics.following(), metrics.finishedBooks(),
                    metrics.totalReadingMinutes(), metrics.currentStreak(), user.createdAt()
            );
        }
    }
}
