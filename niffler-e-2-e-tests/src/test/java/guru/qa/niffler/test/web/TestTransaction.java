package guru.qa.niffler.test.web;


import guru.qa.niffler.data.dao.*;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDBClient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
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
    @Test
    void findAllWithAuthoritiesTest() {
        AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
        List<AuthUserEntity> usersWithAuthorities = authUserDao.findAllWithAuthorities();

        assertNotNull(usersWithAuthorities);
        assertFalse(usersWithAuthorities.isEmpty());

        for (AuthUserEntity user : usersWithAuthorities) {
            System.out.printf("User: %s (%s)%n", user.getUsername(), user.getId());
            if (user.getAuthorities() != null) {
                for (AuthAuthorityEntity authority : user.getAuthorities()) {
                    System.out.printf("  Authority: %s%n", authority.getAuthority());
                }
            } else {
                System.out.println("  No authorities");
            }
        }
    }
}
