package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersApi;
import guru.qa.niffler.service.UsersClient;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Optional;

public class UsersApiClient implements UsersClient {
    private static final Config CFG = Config.getInstance();
    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final UsersApi usersApi = retrofit.create(UsersApi.class);
    private <T> T executeCall(Call<T> call){
        final Response<T> response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        Assertions.assertTrue(response.isSuccessful());
        return response.body();
    }
    @Override
    public UserJson createUser(String username, String password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createFriends(UserJson user, int count) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    @Override
    public void createIncomeInvitations(UserJson requester, int count) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createOutcomeInvitations(UserJson addressee, int count) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<UserJson> findUserByUsername(String username) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
