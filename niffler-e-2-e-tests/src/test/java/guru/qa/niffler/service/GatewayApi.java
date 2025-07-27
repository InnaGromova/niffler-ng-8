package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface GatewayApi {

    @GET("api/categories/all")
    Call<List<CategoryJson>> allCategories(@Header("Authorization") String bearerToken);

    @POST("api/categories/add")
    Call<CategoryJson> addCategory(@Header("Authorization") String bearerToken,
                                   @Body CategoryJson category);
    @GET("api/friends/all")
    Call<List<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                    @Query("searchQuery") @Nullable String searchQuery);
    @DELETE("api/friends/remove")
    Call<Void> removeFriend(@Header("Authorization") String bearerToken,
                            @Query("username") @Nullable String targetUsername);
    @POST("api/invitations/accept")
    Call<UserJson> acceptInvitation(@Header("Authorization") String bearerToken,
                                    @Body UserJson friend);
    @POST("api/invitations/decline")
    Call<UserJson> declineInvitation(@Header("Authorization") String bearerToken,
                                     @Body UserJson friend);
    @GET("api/users/all")
    Call<List<UserJson>> allUsers(@Header("Authorization") String bearerToken,
                                  @Query("searchQuery") @Nullable String searchQuery);
    @POST("api/invitations/send")
    Call<UserJson> sendInvitation(@Header("Authorization") String bearerToken,
                                  @Body UserJson friend);

    @POST("api/spends/add")
    Call<SpendJson> addSpend(@Header("Authorization") String bearerToken,
                             @Body SpendJson spend);

    @PATCH("api/spends/edit")
    Call<SpendJson> editSpend(@Header("Authorization") String bearerToken,
                              @Body SpendJson spend);

    @DELETE("api/spends/remove")
    Call<Void> removeSpends(@Header("Authorization") String bearerToken,
                            @Query("ids") @Nonnull List<String> ids);

    @GET("api/users/current")
    Call<UserJson> currentUser(@Header("Authorization") String bearerToken);

    @POST("api/users/update")
    Call<UserJson> updateUser(@Header("Authorization") String bearerToken,
                              @Body UserJson user);
}
