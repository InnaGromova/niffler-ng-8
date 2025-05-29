package guru.qa.niffler.test.web;

import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.SpendDBClient;
import guru.qa.niffler.service.impl.UserDBClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static guru.qa.niffler.utils.RandomDataUtils.*;
import java.util.Date;
import java.util.stream.Stream;

//public class TestTransaction {
//    @ParameterizedTest
//    @MethodSource("spendRepositoryProvider")
//    void spendTest(SpendRepository repository) throws Exception {
//        SpendDBClient spendDBClient = new SpendDBClient(repository);
//        SpendJson spend = spendDBClient.createSpend(
//                new SpendJson(
//                        null,
//                        new Date(),
//                        new CategoryJson(
//                                null,
//                                randomCategoryName(),
//                                "test-user8",
//                                false
//                        ),
//                        CurrencyValues.RUB,
//                        700.0,
//                        randomSentence(2),
//                        "test-user8"
//                )
//        );
//        Assertions.assertNotNull(spendDBClient.findSpendById(spend.id()));
//        SpendJson newSpend = new SpendJson(
//                spend.id(),
//                new Date(),
//                spend.category(),
//                CurrencyValues.RUB,
//                600.0,
//                "test",
//                spend.username());
//
//        spendDBClient.updateSpend(newSpend);
//        Assertions.assertEquals("test", spendDBClient.findSpendById(spend.id()).description());
//        spendDBClient.deleteSpend(newSpend);
//        Assertions.assertNull(spendDBClient.findSpendById(newSpend.id()));
//    }
//    @ParameterizedTest
//    @MethodSource("userDataRepositoryProvider")
//    void usersDBClientTest(UserDataRepository userDataRepository, AuthUserRepository authRepository) throws Exception {
//        UserDBClient userDBClient = new UserDBClient();
//        String username = randomUserName();
//        UserJson user = userDBClient.createUser(username, "test");
//        userDBClient.createFriends(user, 1);
//        userDBClient.createIncomeInvitations(user, 2);
//        userDBClient.createOutcomeInvitations(user, 3);
//    }
//    static Stream<SpendRepository> spendRepositoryProvider() {
//        return Stream.of(
//                new SpendRepositoryHibernate(),
//                new SpendRepositoryJdbc(),
//                new SpendRepositorySpringJdbc()
//        );
//    }
//
//    static Stream<Arguments> userDataRepositoryProvider() {
//        return Stream.of(
//                Arguments.of(new UserdataUserRepositoryHibernate(), new AuthUserRepositoryHibernate()),
//                Arguments.of(new UserdataUserRepositoryJdbc(), new AuthUserRepositoryJdbc()),
//                Arguments.of(new UserdataUserRepositorySpringJdbc(), new AuthUserRepositorySpringJdbc())
//        );
//    }
//
//    }

