package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.UUID;

public record SpendJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("spendDate")
    Date spendDate,
    @JsonProperty("category")
    CategoryJson category,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("amount")
    Double amount,
    @JsonProperty("description")
    String description,
    @JsonProperty("username")
    String username) {
    public static @Nonnull SpendJson fromEntity(@Nonnull SpendEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("SpendEntity cannot be null");
        }

        final CategoryEntity category = entity.getCategory();
        final String username = entity.getUsername();

        return new SpendJson(
                entity.getId(),
                entity.getSpendDate(),
                category != null ? new CategoryJson(
                        category.getId(),
                        category.getName(),
                        username,
                        category.isArchived()
                ) : null,
                entity.getCurrency(),
                entity.getAmount(),
                entity.getDescription(),
                username
        );
    }

}
