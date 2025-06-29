package guru.qa.niffler.page;



import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;


public class FriendsPage {

    private final SelenideElement messageThereAreNoFriends;
    private final SelenideElement waitingMessage;
    private final SelenideElement searchField;
    public FriendsPage(SelenideDriver driver){
        this.messageThereAreNoFriends = driver.$(byText("There are no users yet"));
        this.waitingMessage = driver.$(byText("Waiting..."));
        this.searchField = driver.$("input[placeholder='Search']");
    }
    public FriendsPage(){
        this.messageThereAreNoFriends = $(byText("There are no users yet"));
        this.waitingMessage = $(byText("Waiting..."));
        this.searchField = $("input[placeholder='Search']");
    }
    public void checkFriendIsEmpty(){
        messageThereAreNoFriends.shouldBe(visible);
    }
    public void checkFriendWithSelectedUsername(String username, SelenideDriver driver) {
        findUser(username);
        messageThereAreNoFriends.shouldNotBe(visible);
        driver.$(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);
    }
    public void checkOutcomeRequest(String username, SelenideDriver driver){
        findUser(username);
        System.out.println("Username: " + username);
        messageThereAreNoFriends.shouldBe(visible);
        driver.$(By.xpath("//h2[text()='All people']")).click();
        driver.$(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);
        waitingMessage.shouldBe(visible);
    }
    public void checkIncomeRequest(String username, SelenideDriver driver){
        findUser(username);
        driver.$(By.xpath("//p[text()='" + username + "']")).shouldBe(visible);

    }
    public void findUser(String username) {
        searchField
                .setValue(username)
                .pressEnter();
    }
}
