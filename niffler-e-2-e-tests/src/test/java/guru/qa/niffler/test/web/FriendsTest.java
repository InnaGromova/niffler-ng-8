package guru.qa.niffler.test.web;


import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.open;


@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsTest {
    private static final Config CFG = Config.getInstance();

    @Test
    @User
    void checkFriendsTableIsEmpty(UserJson user) {
        System.out.println("Testing with user: " + user.username());
         open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        new FriendsPage().checkFriendIsEmpty();
    }
    @Test
    @User(withFriend = 1)
    void checkFriendsTableForUserWithFriend(UserJson user){
        System.out.println("Testing with user: " + user.username());
        open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        new FriendsPage().checkFriendWithSelectedUsername(String.valueOf(user.testData().friends().getFirst().username()));
    }
    @Test
    @User(withOutInvite = 1)
    void checkFriendsTableForUserWithOutcomeRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        new FriendsPage().checkOutcomeRequest(String.valueOf(user.testData().friendshipRequests().getFirst().username()));
    }
    @Test
    @User(withInInvite = 1)
    void checkFriendsTableForUserWithIncomeRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends();
        new FriendsPage().checkIncomeRequest(String.valueOf(user.testData().friendshipAddressees().getFirst().username()));
    }
    @Test
    @User(withInInvite = 1)
    void checkAcceptingFriendRequest(UserJson user){
        System.out.println("Login: " + user.username());
        System.out.println("Password: " + user.testData().password());
        open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends()
                .checkIncomeRequest(String.valueOf(user.testData().friendshipAddressees().getFirst().username()))
                .clickAcceptButtonForUser(String.valueOf(user.testData().friendshipAddressees().getFirst().username()))
                .checkFriendWithSelectedUsername(String.valueOf(user.testData().friendshipAddressees().getFirst().username()));
    }
    @Test
    @User(withInInvite = 1)
    void checkRejectFriendRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openFriends()
                .checkIncomeRequest(String.valueOf(user.testData().friendshipAddressees().getFirst().username()))
                .clickRejectFriendshipButtonForName(String.valueOf(user.testData().friendshipAddressees().getFirst().username()))
                .checkFriendIsEmpty();
    }
}
