package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsTest {
    private static final Config CFG = Config.getInstance();
    FriendsPage friendsPage = new FriendsPage();

    @Test
    @User
    void checkFriendsTableIsEmpty(UserJson user) {
        System.out.println("Testing with user: " + user.username());
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        friendsPage.checkFriendIsEmpty();
    }
    @Test
    @User(withFriend = 1)
    void checkFriendsTableForUserWithFriend(UserJson user){
        System.out.println("Testing with user: " + user.username());
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        friendsPage.checkFriendWithSelectedUsername(String.valueOf(user.testData().friends().getFirst().username()));
    }
    @Test
    @User(withOutInvite = 1)
    void checkFriendsTableForUserWithOutcomeRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        friendsPage.checkOutcomeRequest(String.valueOf(user.testData().friendshipRequests().getFirst().username()));
    }
    @Test
    @User(withInInvite = 1)
    void checkFriendsTableForUserWithIncomeRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        friendsPage.checkIncomeRequest(String.valueOf(user.testData().friendshipAddressees().getFirst().username()));

    }
}
