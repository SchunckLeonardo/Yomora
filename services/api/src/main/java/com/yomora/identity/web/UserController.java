package com.yomora.identity.web;

import com.yomora.identity.domain.User;
import com.yomora.identity.domain.UserRepository;
import com.yomora.identity.application.UpdateProfileCommand;
import com.yomora.identity.application.UserProfileService;
import com.yomora.identity.application.ProfileMetrics;
import com.yomora.identity.application.ProfileMetricsService;
import com.yomora.identity.application.ProfilePhotoService;
import com.yomora.identity.application.ProfilePhotoUpload;
import com.yomora.moderation.domain.ModerationRepository;
import com.yomora.shared.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
class UserController {
    private final UserRepository userRepository;
    private final UserProfileService profileService;
    private final ProfileMetricsService metricsService;
    private final ProfilePhotoService profilePhotoService;
    private final ModerationRepository moderationRepository;
    private final CurrentUser currentUser;

    UserController(UserRepository userRepository, UserProfileService profileService,
                   ProfileMetricsService metricsService, ProfilePhotoService profilePhotoService,
                   ModerationRepository moderationRepository,
                   CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.profileService = profileService;
        this.metricsService = metricsService;
        this.profilePhotoService = profilePhotoService;
        this.moderationRepository = moderationRepository;
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    UserResponse me(Authentication authentication) {
        User user = userRepository.findById(currentUser.id(authentication))
                .orElseThrow(() -> new UserNotFoundException(currentUser.id(authentication)));
        return UserResponse.from(user, metricsService.forUser(user.id()), profilePhotoService.readUrl(user.avatarUrl()));
    }

    @PatchMapping("/me")
    UserResponse update(@Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
        User user = profileService.update(currentUser.id(authentication), new UpdateProfileCommand(
                request.name(), request.username(), request.bio(), request.publicProfile()
        ));
        return UserResponse.from(user, metricsService.forUser(user.id()), profilePhotoService.readUrl(user.avatarUrl()));
    }

    @PostMapping("/me/avatar/upload-url")
    ProfilePhotoUpload createAvatarUpload(
            Authentication authentication,
            @Valid @RequestBody AvatarUploadRequest request
    ) {
        return profilePhotoService.requestUpload(
                currentUser.id(authentication), request.contentType(), request.contentLength()
        );
    }

    @PostMapping("/me/avatar/complete")
    UserResponse completeAvatarUpload(
            Authentication authentication,
            @Valid @RequestBody CompleteAvatarUploadRequest request
    ) {
        User user = profilePhotoService.completeUpload(currentUser.id(authentication), request.objectKey());
        return UserResponse.from(user, metricsService.forUser(user.id()), profilePhotoService.readUrl(user.avatarUrl()));
    }

    @DeleteMapping("/me")
    ResponseEntity<Void> delete(Authentication authentication) {
        profileService.delete(currentUser.id(authentication));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    PublicUserResponse profile(Authentication authentication, @PathVariable UUID id) {
        if (moderationRepository.isBlockedEitherWay(currentUser.id(authentication), id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil indisponível");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return PublicUserResponse.from(
                user, metricsService.forUser(user.id()), profilePhotoService.readUrl(user.avatarUrl())
        );
    }

    record UpdateProfileRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(min = 3, max = 40)
            @Pattern(regexp = "^[\\p{L}0-9._-]+$") String username,
            @Size(max = 500) String bio,
            boolean publicProfile
    ) {
    }

    record AvatarUploadRequest(
            @NotBlank String contentType,
            @Min(1) @Max(5 * 1024 * 1024) long contentLength
    ) {
    }

    record CompleteAvatarUploadRequest(@NotBlank @Size(max = 500) String objectKey) {
    }

    record PublicUserResponse(
            UUID id, String name, String username, String bio, String avatarUrl, boolean publicProfile,
            long followers, long following, long finishedBooks, long totalReadingMinutes, int currentStreak,
            Instant createdAt
    ) {
        static PublicUserResponse from(User user, ProfileMetrics metrics, String avatarUrl) {
            return new PublicUserResponse(user.id(), user.name(), user.username(), user.bio(), avatarUrl,
                    user.publicProfile(), metrics.followers(), metrics.following(), metrics.finishedBooks(),
                    metrics.totalReadingMinutes(), metrics.currentStreak(), user.createdAt());
        }
    }

    record UserResponse(
            UUID id,
            String name,
            String username,
            String email,
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
        static UserResponse from(User user, ProfileMetrics metrics, String avatarUrl) {
            return new UserResponse(
                    user.id(), user.name(), user.username(), user.email(), user.bio(), avatarUrl,
                    user.publicProfile(), metrics.followers(), metrics.following(), metrics.finishedBooks(),
                    metrics.totalReadingMinutes(), metrics.currentStreak(), user.createdAt()
            );
        }
    }
}
