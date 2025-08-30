package guru.qa.niffler.test.web.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.type.CurrencyValues;
import guru.qa.niffler.model.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static guru.qa.niffler.utils.UtilsStat.calculateCategorySums;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class StatGraphQlTest extends BaseGraphQlTest {

    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                0.0,
                result.total
        );
    }
    @Test
    @User
    @ApiLogin
    public void allCurrenciesShouldBeReturnedFromGateway(@Token String bearerToken) {
        final ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient.query(new CurrenciesQuery())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<CurrenciesQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();

        final CurrenciesQuery.Data data = response.dataOrThrow();

        final List<CurrenciesQuery.Currency> all = data.currencies;

        Assertions.assertEquals(
                Currency.RUB.name(),
                all.get(0).currency.rawValue
        );
        Assertions.assertEquals(
                Currency.KZT.name(),
                all.get(1).currency.rawValue
        );
        Assertions.assertEquals(
                Currency.EUR.name(),
                all.get(2).currency.rawValue
        );
        Assertions.assertEquals(
                Currency.USD.name(),
                all.get(3).currency.rawValue
        );
    }
    @Test
    @ApiLogin
    @User(
            spendings = {
                    @Spend(
                            category = "Тест 1",
                            description = "Тестовая категория 1",
                            amount = 2000.00,
                            currency = Currency.RUB
                    ),
                    @Spend(
                            category = "Тест 2",
                            description = "Тестовая категория 2",
                            amount = 3000.00,
                            currency = Currency.EUR
                    )
            }

    )
    void statShouldConvertCurrencies(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> statCall =
                apolloClient.query(StatQuery.builder()
                                .statCurrency(CurrencyValues.safeValueOf(Currency.USD.name()))
                                .build())
                        .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response =
                Rx2Apollo.single(statCall).blockingGet();

        assertEquals(CurrencyValues.safeValueOf(Currency.USD.name()), response.data.stat.currency);
        assertTrue(response.data.stat.total > 0);

        response.data.stat.statByCategories.forEach(stat ->
                assertEquals(CurrencyValues.safeValueOf(Currency.USD.name()), stat.currency)
        );
    }
    @Test
    @ApiLogin
    @User(
            categories = {
                    @Category(name = "Работа", archived = false),
                    @Category(name = "Рисование", archived = true),
                    @Category(name = "Кино", archived = false),
                    @Category(name = "Косметика", archived = true),

            },
            spendings = {
                    @Spend(category = "Работа", description = "Курсы программировния", amount = 30000, currency = Currency.EUR),
                    @Spend(category = "Рисование", description = "Краски", amount = 6000, currency = Currency.RUB),
                    @Spend(category = "Косметика", description = "Профессиональная", amount = 100000, currency = Currency.RUB),
                    @Spend(category = "Кино", description = "Билеты в кино", amount = 400, currency = Currency.RUB)
            }
    )
    void statShouldExcludeArchivedCategories(@Token String bearerToken, UserJson user) {
        Map<String, Double> expectedCategoriesWithSum = calculateCategorySums(user);

        final ApolloCall<StatQuery.Data> stat = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(stat).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();

        List<String> actualCategoryNames = data.stat.statByCategories.stream()
                .map(statByCategory -> statByCategory.categoryName)
                .toList();

        final StatQuery.StatByCategory actualArchivedStat = data.stat.statByCategories.stream()
                .filter(c -> c.categoryName.equals("Archived"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Archived category not found in stats"));

        assertAll("archived categories",
                () -> assertFalse(actualCategoryNames.contains("Рисование"), "Archived categories should be grouped under 'Archived'"),
                () -> assertFalse(actualCategoryNames.contains("Косметика"), "Archived categories should be grouped under 'Archived'"),
                () -> assertEquals(expectedCategoriesWithSum.get("Archived"), actualArchivedStat.sum));
    }

}
