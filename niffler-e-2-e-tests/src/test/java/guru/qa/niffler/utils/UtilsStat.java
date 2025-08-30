package guru.qa.niffler.utils;

import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;

import java.util.Map;
import java.util.stream.Collectors;

public class UtilsStat {
    public static Map<String, Double> calculateCategorySums(UserJson user) {
        return user.testData().spendings().stream()
                .collect(Collectors.groupingBy(
                        spend -> spend.category().archived() ? "Archived" : spend.category().name(),
                        Collectors.summingDouble(SpendJson::amount)
                ));
    }
}
