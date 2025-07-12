package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.AuthApi;
import guru.qa.niffler.utils.OAuthUtils;
import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.ThreadSafeCookieStore;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AuthApiClient extends RestClient {
    private final AuthApi authApi;
    public AuthApiClient() {
        super(CFG.authUrl());
        this.authApi = create(AuthApi.class);
    }
    @SneakyThrows
    public String login(@Nonnull String username,
                        @Nonnull String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String clientId = "client";

        Response<Void> authResponse = authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ).execute();
        assertEquals(302, authResponse.code());

        Response<Void> loginResponse = authApi.login(
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN"),
                username,
                password
        ).execute();
        assertEquals(302, loginResponse.code());

        String locationUrl = loginResponse.headers().get("Location");

        String code = StringUtils.substringAfter(locationUrl, "code=");

        Response<JsonNode> tokenResponse = authApi.token(
                code,
                redirectUri,
                codeVerifier,
                "authorization_code",
                clientId

        ).execute();
        assertEquals(200, tokenResponse.code());

        return tokenResponse.body().get("id_token").asText();
    }

    public String preRequest(){
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        execute(authApi.authorize(
                "code",
                "client",
                "openid",
                CFG.frontUrl() + "authorized",
                codeChallenge,
                "S256")
        );
        return codeVerifier;
    }
    public String token(String code, String codeVerifier) throws IOException {
        String body = String.valueOf(execute(
                authApi.token(
                        code,
                        CFG.frontUrl() + "authorized",
                        codeVerifier,
                        "authorization_code",
                        "client"
                )));
        Assertions.assertNotNull(body);
        return new ObjectMapper()
                .readTree(body.getBytes(StandardCharsets.UTF_8))
                .get("id_token")
                .asText();
    }

}
