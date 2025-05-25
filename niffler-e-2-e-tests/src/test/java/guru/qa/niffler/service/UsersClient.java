package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.Optional;

public interface UsersClient {

    UserJson createUser(String username, String password);

    void createFriends(UserJson user, int count) throws Exception;

    void createIncomeInvitations(UserJson requester, int count) throws Exception;

    void createOutcomeInvitations(UserJson addressee, int count) throws Exception;

    Optional<UserJson> findUserByUsername(String username) throws Exception;
}
