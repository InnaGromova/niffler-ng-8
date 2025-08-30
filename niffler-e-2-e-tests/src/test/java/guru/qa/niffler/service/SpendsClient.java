package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SpendsClient {
    SpendJson createSpend(SpendJson spend);
    SpendJson updateSpend(SpendJson spend) throws Exception;
    CategoryJson createCategory(CategoryJson category);

    CategoryJson updateCategory(CategoryJson category);
    void deleteSpend(SpendJson spendJson) throws Exception;
    SpendJson findSpendById(UUID id) throws Exception;

    List<SpendJson> getAllSpends(String username,
                                 @Nullable CurrencyValues filterCurrency,
                                 @Nullable Date from,
                                 @Nullable Date to);

    List<CategoryJson> getAllCategories(String username, boolean excludeArchived);
}
