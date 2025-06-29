package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsTest {
    private static final Config CFG = Config.getInstance();
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

    @Test
    @User
    void checkFriendsTableIsEmpty(UserJson user) {
        System.out.println("Testing with user: " + user.username());
        driver.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        new FriendsPage().checkFriendIsEmpty();
    }
    @Test
    @User(withFriend = 1)
    void checkFriendsTableForUserWithFriend(UserJson user){
        System.out.println("Testing with user: " + user.username());
        driver.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        new FriendsPage().checkFriendWithSelectedUsername(String.valueOf(user.testData().friends().getFirst().username()),driver);
    }
    @Test
    @User(withOutInvite = 1)
    void checkFriendsTableForUserWithOutcomeRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        driver.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        new FriendsPage().checkOutcomeRequest(String.valueOf(user.testData().friendshipRequests().getFirst().username()),driver);
    }
    @Test
    @User(withInInvite = 1)
    void checkFriendsTableForUserWithIncomeRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        driver.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        new FriendsPage().checkIncomeRequest(String.valueOf(user.testData().friendshipAddressees().getFirst().username()),driver);
    }
}
