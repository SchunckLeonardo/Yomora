package com.yomora.identity.application;

public record UpdateProfileCommand(
        String name,
        String username,
        String bio,
        boolean publicProfile
) {
}
