package guru.qa.niffler.service.impl;


import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.Currency;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.SpendsApi;
import guru.qa.niffler.service.SpendsClient;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Assertions;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

public class SpendApiClient extends RestClient implements SpendsClient {
    private static final Config CFG = Config.getInstance();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(
                    new AllureOkHttp3()
                            .setRequestTemplate("request.ftl")
                            .setResponseTemplate("response.ftl")
            ).build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final SpendsApi spendApi = retrofit.create(SpendsApi.class);
    public SpendApiClient() {
        super(CFG.spendUrl());
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
    @Override
    public SpendJson createSpend(SpendJson spend) {
        if (spend.category().id() == null) {
            createCategory(spend.category());
        }
        return executeCall(spendApi.addSpend(spend));
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) throws Exception {
        return executeCall(spendApi.editSpend(spend));
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return executeCall(spendApi.addCategory(category));
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteSpend(SpendJson spendJson) throws Exception {
        executeCall(spendApi.removeSpend(spendJson.username(), Collections.singletonList(spendJson.id().toString())));
    }

    @Override
    public SpendJson findSpendById(UUID id) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<SpendJson> getAllSpends(String username,
                                        @Nullable Currency filterCurrency,
                                        @Nullable Date from,
                                        @Nullable Date to) {
        return Objects.requireNonNull(execute(spendApi.getAllSpends(username, filterCurrency, from, to)));
    }
    @Override
    public List<CategoryJson> getAllCategories(String username, boolean excludeArchived) {
        try {
            Response<List<CategoryJson>> response = spendApi.getCategories(username, excludeArchived).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Request failed with status code: " + response.code());
            }
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException("Error during HTTP request", e);
        }
    }
}
