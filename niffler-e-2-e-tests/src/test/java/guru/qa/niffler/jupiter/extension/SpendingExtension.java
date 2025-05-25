package guru.qa.niffler.jupiter.extension;
import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendsClient;
import guru.qa.niffler.service.impl.SpendDBClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
   private final SpendsClient spendDBClient = new SpendDBClient();
    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(useranno -> {
                    if (ArrayUtils.isNotEmpty(useranno.spendings())){
                        UserJson createdUser = UserExtension.createUser();
                        final String username = createdUser != null
                                ? createdUser.username()
                                : useranno.username();

                        final List<SpendJson> createdSpending = new ArrayList<>();

                        for (Spend spendanno : useranno.spendings()){
                            SpendJson spend = new SpendJson(
                                    null,
                                    new Date(),
                                    new CategoryJson(
                                            null,
                                            spendanno.category(),
                                            username,
                                            false
                                    ),
                                    CurrencyValues.RUB,
                                    spendanno.amount(),
                                    spendanno.description(),
                                    username
                            );
                            createdSpending.add(spendDBClient.createSpend(spend));
                        }
                        if (createdUser != null) {
                            createdUser.testData().spendings().addAll(
                                    createdSpending
                            );
                        } else {
                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    createdSpending
                            );
                        }
                    }
                });
    }
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
    }
    @Override
    @SuppressWarnings("unchecked")
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (SpendJson[]) extensionContext.getStore(SpendingExtension.NAMESPACE).get(extensionContext.getUniqueId(), List.class)
                .stream()
                .toArray(SpendJson[]::new);
    }
}