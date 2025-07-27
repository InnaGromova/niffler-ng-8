package guru.qa.niffler.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TestData(
        @JsonIgnore @NotNull String password,
        @JsonIgnore @NotNull List<CategoryJson> categories,
        @JsonIgnore @NotNull List<SpendJson> spendings,
        @JsonIgnore @NotNull List<UserJson> friends,
        @JsonIgnore @NotNull List<UserJson> incomeInvitations,
        @JsonIgnore @NotNull List<UserJson> outcomeInvitations) {
}
