package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UserDBClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;

import static guru.qa.niffler.utils.RandomDataUtils.randomUserName;


public class UserExtension implements BeforeEachCallback, ParameterResolver {
    private final UsersClient usersClient = new UserDBClient();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String defaultPassword = "12345";
    @Override
    public void beforeEach(ExtensionContext context)  {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())){
                        final String username = randomUserName();

                        UserJson user = usersClient.createUser(
                                username,
                                defaultPassword
                        );
                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                user.withPassword(defaultPassword)
                        );
                    }
                }
                );
    }
    public static @Nullable UserJson createUser(){
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return null;
    }
}
