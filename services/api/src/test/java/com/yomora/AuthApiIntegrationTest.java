package com.yomora;

import com.jayway.jsonpath.JsonPath;
import com.yomora.identity.application.ExternalIdentityAuthenticator;
import com.yomora.identity.application.VerifiedExternalIdentity;
import com.yomora.identity.application.ExternalAccountEvent;
import com.yomora.identity.application.ExternalAccountEventType;
import com.yomora.identity.application.ExternalAccountEventVerifier;
import com.yomora.identity.domain.ExternalProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ExternalIdentityAuthenticator externalIdentityAuthenticator;

    @MockitoBean
    ExternalAccountEventVerifier externalAccountEventVerifier;

    @Test
    void registerReturnsTokensThatAuthorizeTheCurrentUserEndpoint() throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Marina Rainha",
                                  "username": "marina-integration",
                                  "email": "marina-integration@example.com",
                                  "password": "uma-senha-segura"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = JsonPath.read(response, "$.accessToken");
        String tokenType = JsonPath.read(response, "$.tokenType");
        assertThat(tokenType).isEqualTo("Bearer");

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("marina-integration@example.com"))
                .andExpect(jsonPath("$.username").value("marina-integration"));
    }

    @Test
    void servesTheAppleAppSiteAssociationWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/.well-known/apple-app-site-association"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applinks.details[0].appID").value("FMWWTVS6HZ.app.yomora.ios"))
                .andExpect(jsonPath("$.applinks.details[0].paths[0]")
                        .value("/api/v1/auth/email-verification/confirm*"));
    }

    @Test
    void appleSignInCreatesAProfileAndReturnsYomoraTokens() throws Exception {
        when(externalIdentityAuthenticator.verify(any())).thenReturn(new VerifiedExternalIdentity(
                ExternalProvider.APPLE,
                "apple-integration-subject",
                "apple-integration@example.com",
                true,
                "apple-refresh"
        ));

        String response = mockMvc.perform(post("/api/v1/auth/apple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identityToken": "signed-identity-token",
                                  "authorizationCode": "single-use-code",
                                  "nonce": "raw-nonce",
                                  "fullName": "Marina Apple"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = JsonPath.read(response, "$.accessToken");
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("apple-integration@example.com"))
                .andExpect(jsonPath("$.profileComplete").value(false));

        mockMvc.perform(get("/api/v1/users/me/access-methods")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").value(false))
                .andExpect(jsonPath("$.providers[0]").value("APPLE"));

        when(externalAccountEventVerifier.verify("signed-events-token")).thenReturn(new ExternalAccountEvent(
                ExternalProvider.APPLE, "apple-integration-subject", ExternalAccountEventType.CONSENT_REVOKED
        ));
        mockMvc.perform(post("/api/v1/auth/apple/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payload\":\"signed-events-token\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
}
