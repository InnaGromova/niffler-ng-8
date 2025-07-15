package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import javax.annotation.Nullable;
import java.util.List;

public interface UserApi {
    @GET("internal/users/current")
    Call<UserJson> getUser(
            @Query("username") String username
    );
    @GET("internal/users/current")
    UserJson currentUser(@Query("username") String username);
    @GET("/internal/users/all")
    Call<List<UserJson>> all(
            @Query("username") String username,
            @Query ("searchQuery") String searchQuery);
    @GET("/internal/friends/all")
    Call<List<UserJson>> getFriends(@Query("username") String username);
}
