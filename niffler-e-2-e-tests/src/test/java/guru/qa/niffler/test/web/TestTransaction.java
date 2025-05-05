package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDBClient;
import org.junit.jupiter.api.Test;

public class TestTransaction {
    @Test
    void testDbFunction() {
        UserJson user = new UserJson(
                "test-user8",
                "Тест8",
                "Тестовый8",
                "Тестовый8 Тест8",
                null,
                null,
                null,
                "test8"
        );

        new UserDBClient().createUser(user);
    }
}
