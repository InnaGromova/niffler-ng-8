package guru.qa.niffler.jupiter.extension;

import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendsClient;
import guru.qa.niffler.service.impl.SpendDBClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendsClient spendDBClient = new SpendDBClient();
    private final Random random = new Random();

    @Override
    public void beforeEach(ExtensionContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ExtensionContext cannot be null");
        }
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(useranno -> {
            if (ArrayUtils.isNotEmpty(useranno.categories())){
                UserJson createdUser = UserExtension.createUser();
                final String username = createdUser != null
                        ? createdUser.username()
                        : useranno.username();
                final List<CategoryJson> createdCategories = new ArrayList<>();
                    for (Category categoryAnno : useranno.categories()){
                        CategoryJson category = new CategoryJson(
                                null,
                                categoryAnno.name(),
                                username,
                                categoryAnno.archived()
                        );
                        createdCategories.add(spendDBClient.createCategory(category));
                    }

                    if (createdUser != null) {
                        createdUser.testData().categories().addAll(
                                createdCategories
                        );
                    } else {
                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                createdCategories
                        );
                    }
                }
            });
        }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
    }
    @Override
    @SuppressWarnings("unchecked")
    public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (CategoryJson[]) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), List.class)
                .stream()
                .toArray(CategoryJson[]::new);
    }
//    @Override
//    public void afterTestExecution(ExtensionContext context) {
//        CategoryJson categoryFromStore =
//                context.getStore(CategoryExtension.NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
//        if (categoryFromStore == null) {
//            throw new IllegalStateException("Category is not initialized");
//        }
//        System.out.println("Category ID: " + categoryFromStore.id());
//        CategoryJson categoryToBeArchived = new CategoryJson(
//                categoryFromStore.id(),
//                categoryFromStore.name(),
//                categoryFromStore.username(),
//                true
//        );
//        spendDBClient.updateCategory(categoryToBeArchived);
//    }
}