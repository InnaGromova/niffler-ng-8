package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.UUID;

public interface SpendsClient {
    SpendJson createSpend(SpendJson spend);
    SpendJson updateSpend(SpendJson spend) throws Exception;
    CategoryJson createCategory(CategoryJson category);

    CategoryJson updateCategory(CategoryJson category);
    void deleteSpend(SpendJson spendJson) throws Exception;
    SpendJson findSpendById(UUID id) throws Exception;
}
