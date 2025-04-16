package guru.qa.niffler.jupiter.extension;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import guru.qa.niffler.jupiter.annotation.UserType;

public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(String userName, String password, String friend, String income, String outcome) {}

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_FRIEND = new ConcurrentLinkedDeque<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_FRIEND = new ConcurrentLinkedDeque<>();

    static {
        EMPTY_USERS.add(new StaticUser("test-user4", "test4", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("test-user2", "test2", "test-user1", null, null));
        WITH_INCOME_REQUEST_FRIEND.add(new StaticUser("test-user3", "test3", null, "test-user5", null));
        WITH_OUTCOME_REQUEST_FRIEND.add(new StaticUser("test-user5", "test5", null, null, "test-user3"));

        System.out.println("Initialized queues:");
        System.out.println("EMPTY_USERS: " + EMPTY_USERS);
        System.out.println("WITH_FRIEND_USERS: " + WITH_FRIEND_USERS);
        System.out.println("WITH_INCOME_REQUEST_FRIEND: " + WITH_INCOME_REQUEST_FRIEND);
        System.out.println("WITH_OUTCOME_REQUEST_FRIEND: " + WITH_OUTCOME_REQUEST_FRIEND);
    }
    @Override
    public void beforeEach(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(param -> AnnotationSupport.isAnnotated(param, UserType.class))
                .filter(param -> param.getType().equals(StaticUser.class))
                .map(param -> param.getAnnotation(UserType.class))
                .forEach(userType -> {
                    StopWatch stopWatch = StopWatch.createStarted();
                    StaticUser user = null;
                    while (user == null && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
                        user = getQueue(userType).poll();
                    }
                    if (user == null) {
                        System.err.println("Timeout while waiting for user with type: " + userType.value());
                        throw new IllegalStateException("User not found");
                    }
                    Optional.ofNullable(user).ifPresentOrElse(
                            u -> {
                                Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                                        .getOrComputeIfAbsent(context.getUniqueId(), key -> new HashMap<>());
                                userMap.put(userType, u);
                            },
                            () -> {
                                throw new IllegalStateException("User not found");
                            });
                });
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Map<UserType, StaticUser> users = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        if (users != null) {
            users.forEach((key, value) -> getQueue(key).add(value));
        }
    }
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> parameterType = parameterContext.getParameter().getType();
        System.out.println("Parameter type: " + parameterType.getName());
        boolean supports = parameterType.isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
        return supports;
    }
    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Map<UserType, StaticUser> users = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
        if (users == null) {
            throw new ParameterResolutionException("No users found in the store");
        }
        UserType userType = AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class).orElseThrow();
        return users.get(userType);
    }

    private Queue<StaticUser> getQueue(UserType userType) {
        return switch (userType.value()) {
            case EMPTY -> EMPTY_USERS;
            case WITH_FRIEND -> WITH_FRIEND_USERS;
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_FRIEND;
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_FRIEND;
        };
    }
}