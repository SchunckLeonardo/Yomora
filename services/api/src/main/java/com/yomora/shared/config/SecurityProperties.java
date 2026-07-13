package com.yomora.shared.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("yomora.security")
public record SecurityProperties(
        @NotBlank String issuer,
        @NotBlank String secret,
        @Min(1) long accessTokenMinutes,
        @Min(1) long refreshTokenDays
) {
}
