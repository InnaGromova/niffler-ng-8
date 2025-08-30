package guru.qa.niffler.service.impl;


import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.page.RestResponsePage;
import guru.qa.niffler.service.GatewayV2Api;
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

import static java.util.Objects.requireNonNull;

public class GatewayV2ApiClient extends RestClient {
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
    private final GatewayV2Api gatewayV2Api = retrofit.create(GatewayV2Api.class);
    public GatewayV2ApiClient() {
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
    @Step("Get all friends & income invitations using /api/v2/friends/all endpoint")
    @Nonnull
    public RestResponsePage<UserJson> allFriends(String bearerToken,
                                                 int page,
                                                 int size,
                                                 @Nullable String searchQuery) {
        return requireNonNull(execute(gatewayV2Api.allFriends(bearerToken, page, size, searchQuery), 200));
    }
}
