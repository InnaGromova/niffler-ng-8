package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;

@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsTest {
    private static final Config CFG = Config.getInstance();
    FriendsPage friendsPage = new FriendsPage();

    @Test
    void checkFriendsTableIsEmpty(@UserType(EMPTY) StaticUser user) {
        System.out.println("Testing with user: " + user.userName());
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.userName(), user.password())
                .openFriends();
        friendsPage.checkFriendIsEmpty();
    }
    @Test
    void checkFriendsTableForUserWithFriend(@UserType(WITH_FRIEND) StaticUser user){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.userName(), user.password())
                .openFriends();
        friendsPage.checkFriendWithSelectedUsername(user.friend());
    }
    @Test
    void checkFriendsTableForUserWithOutcomeRequest(@UserType(WITH_OUTCOME_REQUEST) StaticUser user){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.userName(), user.password())
                .openFriends();
        friendsPage.checkOutcomeRequest(user.outcome());
    }
    @Test
    void checkFriendsTableForUserWithIncomeRequest(@UserType(WITH_INCOME_REQUEST) StaticUser user){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.userName(), user.password())
                .openFriends();
        friendsPage.checkIncomeRequest(user.income());

    }
}
