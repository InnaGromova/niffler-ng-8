package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.Order;

import java.util.List;
public class ConsistentTestExecution {
    @Order(2)
    @Test
     void testForNotEmptyList() {
        UsersClient usersClient = new UsersApiClient();
        List<UserJson> list = usersClient.allUsers(RandomData.randomUserName(), null);
        Assertions.assertTrue(list.isEmpty());
    }
    @Order(1)
    @Test
    void testForEmptyList() {
        UsersClient usersClient = new UsersApiClient();
        List<UserJson> list = usersClient.allUsers(RandomData.randomUserName(),null);
        Assertions.assertFalse(list.isEmpty());
    }
}
