package com.yomora.shared.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class AppleAppSiteAssociationController {
    private final String appId;

    AppleAppSiteAssociationController(
            @Value("${yomora.apple.team-id:FMWWTVS6HZ}") String teamId,
            @Value("${yomora.apple.bundle-id:app.yomora.ios}") String bundleId
    ) {
        String resolvedTeamId = teamId == null || teamId.isBlank() ? "FMWWTVS6HZ" : teamId;
        this.appId = resolvedTeamId + "." + bundleId;
    }

    @GetMapping(value = "/.well-known/apple-app-site-association", produces = "application/json")
    Association association() {
        return new Association(new AppLinks(List.of(), List.of(new Detail(
                appId, List.of("/api/v1/auth/email-verification/confirm*")
        ))));
    }

    record Association(AppLinks applinks) {
    }

    record AppLinks(List<String> apps, List<Detail> details) {
    }

    record Detail(String appID, List<String> paths) {
    }
}
