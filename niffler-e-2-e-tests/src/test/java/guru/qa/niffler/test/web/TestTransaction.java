package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDBClient;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class TestTransaction {

    @Test
    void springJdbcTest() {
        UserDBClient udb = new UserDBClient();

        UserJson user = udb.chainedTxCreateUserSpringJdbc(
                        new UserJson(
                                UUID.randomUUID(),
                                "test-user9",
                                "Тест9",
                                "Тестовый9",
                                "Тестовый9 Тест9",
                                CurrencyValues.RUB,
                                "Тест9",
                                "Тест9",
                                "test9"
                        )
                );
        System.out.println(user);
    }
//    @Test
//    void testDbFunction() {
//        UserJson user = new UserJson(
//                "test-user8",
//                "Тест8",
//                "Тестовый8",
//                "Тестовый8 Тест8",
//                null,
//                null,
//                null,
//                "test8"
//        );
//
//        new UserDBClient().createUser(user);
//    }
}
