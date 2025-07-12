package guru.qa.niffler.test.web;

import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {
    private final AuthApiClient authClient = new AuthApiClient();
    @Test
    void oAuthTest() throws IOException {
        String codeVerifier = authClient.preRequest();
        assertNotNull(codeVerifier);
        String code = authClient.login("test-user4", "test4");
        assertNotNull(code);
        String token = authClient.token(code, codeVerifier);
        assertNotNull(token);
        System.out.println("Token: " + token);
    }
}
