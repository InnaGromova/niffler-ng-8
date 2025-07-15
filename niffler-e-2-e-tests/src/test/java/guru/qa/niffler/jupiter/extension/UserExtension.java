package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomData;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;


public class UserExtension implements BeforeEachCallback, ParameterResolver {
    private final UsersClient usersClient = UsersClient.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String defaultPassword = "12345";
    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomData.randomUserName();

                        UserJson user = null;
                        try {
                            user = usersClient.createUser(
                                    username,
                                    defaultPassword
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        List<UserJson> friends = new ArrayList<>();
                        List<UserJson> income = new ArrayList<>();
                        List<UserJson> outcome = new ArrayList<>();

                        if (userAnno.withFriend() > 0) {
                            try {
                                friends = usersClient.createFriends(user, userAnno.withFriend());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (userAnno.withInInvite() > 0) {
                            try {
                                income = usersClient.createIncomeInvitations(user, userAnno.withInInvite());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (userAnno.withOutInvite() > 0) {
                            try {
                                outcome = usersClient.createOutcomeInvitations(user, userAnno.withOutInvite());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        setUser(
                                user.withPassword(
                                        defaultPassword
                                ).withUsers(
                                        friends,
                                        outcome,
                                        income
                                )
                        );
                    }
                });
    }
    public static @Nullable UserJson createUser(){
        final ExtensionContext context = context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getUserJson();
    }

    public static Object getUserJson() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }

    public static void setUser(UserJson testUser) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                testUser
        );
    }
}
