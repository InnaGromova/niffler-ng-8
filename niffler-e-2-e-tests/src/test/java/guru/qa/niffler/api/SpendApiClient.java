package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SpendApiClient {

  private static final Config CFG = Config.getInstance();

  private final OkHttpClient client = new OkHttpClient.Builder().build();
  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .client(client)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  @SneakyThrows
  public SpendJson addSpend(SpendJson spend) {
    return spendApi.addSpend(spend)
        .execute()
        .body();
  }
  public SpendJson createSpend(SpendJson spend)  {
    final Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(201, response.code());
    return response.body();
  }
  public SpendJson editSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend)
              .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
  public SpendJson getSpend(String id, String username) {
    final Response<SpendJson> response;
    try{
      response = spendApi.getSpend(id, username)
              .execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
  public List<SpendJson> getSpends(String username) {
    Response<List<SpendJson>> response;
    try {
      response = spendApi.getSpends(username).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
  public void deleteSpends(String username, List<String> ids) {
    Response<Void> response;
    try {
      response = spendApi.deleteSpends(username, ids).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }
  public CategoryJson addCategory(CategoryJson category)  {
    final Response <CategoryJson> response;
    try {
      response = spendApi.addCategory(category).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
  public CategoryJson updateCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(category)
              .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
  public List<CategoryJson> getCategories(String username, boolean excludedArchived) {
    Response<List<CategoryJson>> response;
    try {
      response = spendApi.getCategories(username, excludedArchived).execute();
    }
    catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
}
