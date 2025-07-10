package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UserDBClient;
import guru.qa.niffler.service.impl.UsersApiClient;

import java.util.List;
import java.util.Optional;

public interface UsersClient {
    static UsersClient getInstance() {
        return "api".equals(System.getProperty("client.impl"))
                ? new UsersApiClient()
                : new UserDBClient();
    }

    UserJson createUser(String username, String password) throws Exception;

    List<UserJson> createFriends(UserJson user, int count) throws Exception;

    List<UserJson> createIncomeInvitations(UserJson requester, int count) throws Exception;

    List<UserJson> createOutcomeInvitations(UserJson addressee, int count) throws Exception;

    Optional<UserJson> findUserByUsername(String username) throws Exception;
}
