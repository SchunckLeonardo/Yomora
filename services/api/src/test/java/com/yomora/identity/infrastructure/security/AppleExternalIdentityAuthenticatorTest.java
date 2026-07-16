package com.yomora.identity.infrastructure.security;

import com.yomora.identity.application.ExternalSignInCommand;
import com.yomora.identity.application.VerifiedExternalIdentity;
import com.yomora.identity.domain.ExternalProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppleExternalIdentityAuthenticatorTest {
    private final CapturingTokenVerifier verifier = new CapturingTokenVerifier();
    private final CapturingAuthorizationCodeClient tokenClient = new CapturingAuthorizationCodeClient();
    private final AppleExternalIdentityAuthenticator authenticator =
            new AppleExternalIdentityAuthenticator(verifier, tokenClient);

    @Test
    void verifiesIdentityAndExchangesTheSingleUseAuthorizationCode() {
        verifier.claims = new AppleIdentityClaims("apple-subject", "reader@example.com", true);
        tokenClient.refreshToken = "apple-refresh-token";

        VerifiedExternalIdentity result = authenticator.verify(new ExternalSignInCommand(
                ExternalProvider.APPLE, "identity-token", "authorization-code", "raw-nonce", "Marina"
        ));

        assertThat(verifier.identityToken).isEqualTo("identity-token");
        assertThat(verifier.rawNonce).isEqualTo("raw-nonce");
        assertThat(tokenClient.authorizationCode).isEqualTo("authorization-code");
        assertThat(result).isEqualTo(new VerifiedExternalIdentity(
                ExternalProvider.APPLE, "apple-subject", "reader@example.com", true, "apple-refresh-token"
        ));
    }

    @Test
    void rejectsAProviderThatIsNotApple() {
        assertThatThrownBy(() -> authenticator.verify(new ExternalSignInCommand(
                null, "identity-token", "authorization-code", "raw-nonce", null
        ))).isInstanceOf(IllegalArgumentException.class);
    }

    private static final class CapturingTokenVerifier implements AppleIdentityTokenVerifier {
        private AppleIdentityClaims claims;
        private String identityToken;
        private String rawNonce;

        @Override
        public AppleIdentityClaims verify(String identityToken, String rawNonce) {
            this.identityToken = identityToken;
            this.rawNonce = rawNonce;
            return claims;
        }
    }

    private static final class CapturingAuthorizationCodeClient implements AppleAuthorizationCodeClient {
        private String authorizationCode;
        private String refreshToken;

        @Override
        public String exchange(String authorizationCode) {
            this.authorizationCode = authorizationCode;
            return refreshToken;
        }
    }
}
