package guru.qa.niffler.service.impl;


import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.GatewayApi;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Assertions;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient extends RestClient {
    private static final Config CFG = Config.getInstance();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(
                    new AllureOkHttp3()
                            .setRequestTemplate("request.ftl")
                            .setResponseTemplate("response.ftl")
            ).build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.gatewayUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final GatewayApi gatewayApi = retrofit.create(GatewayApi.class);
    public GatewayApiClient() {
        super(CFG.gatewayUrl());
    }

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
    @Step("Get all friends & income invitations using /api/friends/all endpoint")
    @Nonnull
    public List<UserJson> allFriends(String bearerToken,
                                     @Nullable String searchQuery) {
        return requireNonNull(execute(gatewayApi.allFriends(bearerToken, searchQuery), 200));
    }
    @Step("Remove friend using /api/friends/remove endpoint")
    public void removeFriend(String bearerToken, String friendUsername) {
        final Response<Void> response;
        try {
            response = gatewayApi.removeFriend(bearerToken, friendUsername).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }
    @Step("Accept invitation using /api/invitations/accept endpoint")
    @Nonnull
    public UserJson acceptInvitation(String bearerToken, UserJson friend) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.acceptInvitation(bearerToken, friend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }
    @Step("Decline invitation using /api/invitations/decline endpoint")
    public UserJson declineInvitation(String bearerToken, UserJson friend) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.declineInvitation(bearerToken, friend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }
    @Step("Get all users using /api/users/all endpoint")
    @Nonnull
    public List<UserJson> allUsers(String bearerToken, @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = gatewayApi.allUsers(bearerToken, searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }
    @Step("Sent invitation using /api/invitations/send endpoint")
    @Nonnull
    public UserJson sendInvitation(String bearerToken, UserJson friend) {
        final Response<UserJson> response;
        try {
            response = gatewayApi.sendInvitation(bearerToken, friend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

}
