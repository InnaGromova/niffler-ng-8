package guru.qa.niffler.page;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


public class FriendsPage {

    private final SelenideElement messageThereAreNoFriends = $(byText("There are no users yet"));;
    private final SelenideElement waitingMessage = $(byText("Waiting..."));
    private final SelenideElement searchField = $("input[placeholder='Search']");
    public void checkFriendIsEmpty(){
        messageThereAreNoFriends.shouldBe(visible);
    }
    public void checkFriendWithSelectedUsername(String username) {
        findUser(username);
        messageThereAreNoFriends.shouldNotBe(visible);
        $(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);
    }
    public void checkOutcomeRequest(String username){
        findUser(username);
        System.out.println("Username: " + username);
        messageThereAreNoFriends.shouldBe(visible);
        $(By.xpath("//h2[text()='All people']")).click();
        $(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);
        waitingMessage.shouldBe(visible);
    }
    public void checkIncomeRequest(String username){
        findUser(username);
        $(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);

    }
    public void findUser(String username) {
        searchField
                .setValue(username)
                .pressEnter();
    }
}
