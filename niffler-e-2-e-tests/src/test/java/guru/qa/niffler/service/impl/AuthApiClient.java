package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.api.CodeInterceptor;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.AuthApi;
import guru.qa.niffler.utils.OAuthUtils;
import guru.qa.niffler.api.ThreadSafeCookieStore;
import lombok.SneakyThrows;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AuthApiClient extends RestClient {
    private final AuthApi authApi;
    public AuthApiClient() {
        super(CFG.authUrl(), true, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }
    @SneakyThrows
    public String login(@Nonnull String username,
                        @Nonnull String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String clientId = "client";

        authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ).execute();

        System.out.println("Cookies before login: " + ThreadSafeCookieStore.INSTANCE.getCookies());

        Response<Void> loginResponse = authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                ).execute();

        System.out.println("Login response code: " + loginResponse.code());
        System.out.println("Login response headers: " + loginResponse.headers());
        System.out.println("Login response body: " + loginResponse.body());


        Response<JsonNode> tokenResponse = authApi.token(
                ApiLoginExtension.getCode(),
                redirectUri,
                clientId,
                codeVerifier,
                "authorization_code"
        ).execute();

        if (tokenResponse.code() != 200) {
            throw new RuntimeException("Token request failed with status code: " + tokenResponse.code());
        }

        return tokenResponse.body().get("id_token")
                .asText();
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
