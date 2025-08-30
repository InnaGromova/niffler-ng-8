package guru.qa.niffler.service;


import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.page.RestResponsePage;
import org.springframework.data.domain.Page;
import retrofit2.Call;
import retrofit2.http.*;
import javax.annotation.Nullable;


public interface GatewayV2Api {
    @GET("api/v2/friends/all")
    Call<RestResponsePage<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                                @Query("page") int page,
                                                @Query("page") int size,
                                                @Query("searchQuery") @Nullable String searchQuery);
}
