package guru.qa.niffler.test.web;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.open;


@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsTest {

    @Test
    @User
    @ApiLogin
    void checkFriendsTableIsEmpty(UserJson user) {
        System.out.println("Testing with user: " + user.username());
        Selenide.open(FriendsPage.URL, FriendsPage.class)
        .checkFriendIsEmpty();
    }
    @Test
    @User(withFriend = 1)
    @ApiLogin
    void checkFriendsTableForUserWithFriend(UserJson user){
        System.out.println("Testing with user: " + user.username());
        open(FriendsPage.URL, FriendsPage.class)
                .checkFriendWithSelectedUsername(String.valueOf(user.testData().friends().getFirst().username()));
    }
    @Test
    @User(withOutInvite = 1)
    void checkFriendsTableForUserWithOutcomeRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        open(FriendsPage.URL, FriendsPage.class)
                .checkOutcomeRequest(String.valueOf(user.testData().outcomeInvitations().getFirst().username()));
    }
    @Test
    @User(withInInvite = 1)
    @ApiLogin
    void checkFriendsTableForUserWithIncomeRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        open(FriendsPage.URL, FriendsPage.class)
                .checkIncomeRequest(String.valueOf(user.testData().incomeInvitations().getFirst().username()));
    }
    @Test
    @User(withInInvite = 1)
    @ApiLogin
    void checkAcceptingFriendRequest(UserJson user){
        System.out.println("Login: " + user.username());
        System.out.println("Password: " + user.testData().password());
        open(FriendsPage.URL, FriendsPage.class)
                .checkIncomeRequest(String.valueOf(user.testData().incomeInvitations().getFirst().username()))
                .clickAcceptButtonForUser(String.valueOf(user.testData().incomeInvitations().getFirst().username()))
                .checkFriendWithSelectedUsername(String.valueOf(user.testData().incomeInvitations().getFirst().username()));
    }
    @Test
    @User(withInInvite = 1)
    @ApiLogin
    void checkRejectFriendRequest(UserJson user){
        System.out.println("Testing with user: " + user.username());
        open(FriendsPage.URL, FriendsPage.class)
                .checkIncomeRequest(String.valueOf(user.testData().incomeInvitations().getFirst().username()))
                .clickRejectFriendshipButtonForName(String.valueOf(user.testData().incomeInvitations().getFirst().username()))
                .checkFriendIsEmpty();
    }
}
