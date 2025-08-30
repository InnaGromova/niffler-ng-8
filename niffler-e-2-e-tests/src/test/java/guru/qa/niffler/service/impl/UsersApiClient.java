package guru.qa.niffler.service.impl;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.*;
import jaxb.userdata.FriendshipStatus;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

@ParametersAreNonnullByDefault
public class UsersApiClient extends RestClient implements UsersClient {
    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
    private final UserApi userApi = new RestClient.EmtyRestClient(CFG.userdataUrl()).create(UserApi.class);

    public UsersApiClient() {
        super(CFG.userdataUrl());
    }

    @NotNull
    @Override
    public UserJson createUser(String username, String password) {
        authApi.requestRegisterForm();
        authApi.registerUser(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        );
        StopWatch sw = StopWatch.createStarted();
        while (sw.getTime(TimeUnit.SECONDS) < 3) {
            UserJson userJson = userApi.currentUser(username);
            if (userJson != null) {
                return userJson.withPassword(password);
            } else Selenide.sleep(100);
        }
        throw new IllegalStateException("Could not register user");
    }

    @Override
    public List<UserJson> createFriends(UserJson user, int count) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    @Override
    public List<UserJson> createIncomeInvitations(UserJson requester, int count) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson addressee, int count) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<UserJson> findUserByUsername(String username) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public List<UserJson> allUsers(String username, String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = userApi.all(username, searchQuery).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.body() != null ? response.body() : Collections.emptyList();
    }
    @Override
    public List<UserJson> getFriends(String username) {
        return Objects.requireNonNull(execute(
                        userApi.getFriends(username)))
                .stream()
                .filter(userJson ->
                        FriendshipStatus.FRIEND.equals(userJson.friendshipStatus()))
                .toList();
    }
    @Override
    public List<UserJson> getIncomeInvitations(String username) {
        return Objects.requireNonNull(execute(
                        userApi.getFriends(username)))
                .stream()
                .filter(userJson ->
                        FriendshipStatus.INVITE_RECEIVED.equals(userJson.friendshipStatus()))
                .toList();
    }

    @Override
    public List<UserJson> getOutcomeInvitations(String username, String searchQuery) {
        return Objects.requireNonNull(execute(
                        userApi.all(username, searchQuery)))
                .stream()
                .filter(userJson ->
                        FriendshipStatus.INVITE_SENT.equals(userJson.friendshipStatus()))
                .toList();
    }
}
