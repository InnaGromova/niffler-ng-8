package guru.qa.niffler.page;



import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;


public class FriendsPage extends BasePage {
    private static final String asseptFriendshipButton = "button.MuiButton-containedPrimary";
    private static final String rejectFriendshipButton = "button.MuiButton-containedSecondary";
    private final SelenideElement messageThereAreNoFriends;
    private final SelenideElement waitingMessage;
    private final SelenideElement searchField;
    private final SelenideElement friendsTableContainer;
    private final ElementsCollection tableHeaders;
    private final ElementsCollection friendRequestRow;
    private final SelenideElement friendsTable;

    public FriendsPage(){
        this.messageThereAreNoFriends = $(byText("There are no users yet"));
        this.waitingMessage = $(byText("Waiting..."));
        this.searchField = $("input[placeholder='Search']");
        this.friendsTableContainer = $("#simple-tabpanel-friends");
        this.friendRequestRow = friendsTableContainer.$$("#requests tr");
        this.tableHeaders = friendsTableContainer.$$("h2.MuiTypography-h5");
        this.friendsTable = friendsTableContainer.$("#friends");
    }
    @Nonnull
    @Step("Check that there are no friends")
    public void checkFriendIsEmpty(){
        messageThereAreNoFriends.shouldBe(visible);
    }
    @Nonnull
    @Step("Check a selected user is a friend")
    public void checkFriendWithSelectedUsername(String username) {
        findUser(username);
        messageThereAreNoFriends.shouldNotBe(visible);
       $(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);
    }
    @Nonnull
    @Step("Checking for an incoming request from the user")
    public FriendsPage checkIncomeRequest(String username){
        findUser(username);
        $(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);
        return this;
    }
    @Nonnull
    @Step("Checking for an outcome request from the user")
    public void checkOutcomeRequest(String username){
        findUser(username);
        System.out.println("Username: " + username);
        messageThereAreNoFriends.shouldBe(visible);
        $(By.xpath("//h2[text()='All people']")).click();
        $(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);
        waitingMessage.shouldBe(visible);
    }
    @Nonnull
    @Step("Search for a user by name")
    public void findUser(String username) {
        searchField
                .setValue(username)
                .pressEnter();
    }
    @Nonnull
    @Step("Accept a friend request")
    public FriendsPage clickAcceptButtonForUser(String user) {
        friendRequestRow.find(text(user))
                .$(asseptFriendshipButton)
                .click();
        return this;
    }
    @Nonnull
    @Step("Reject a friend request")
    public FriendsPage clickRejectFriendshipButtonForName(String user) {
        friendRequestRow.find(text(user))
                .$(rejectFriendshipButton)
                .click();
        return this;
    }
}
