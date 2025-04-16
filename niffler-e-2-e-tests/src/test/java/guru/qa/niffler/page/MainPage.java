package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement spendingTable = $("#spendings");
  private final SelenideElement headerBlock = $("#root header");
  private final SelenideElement menu = $("ul[role='menu']");
  private final SelenideElement avatar = $("svg[data-testid='PersonIcon']");
  private final SelenideElement profile = $("li[role='menuitem'] a[href='/profile']");
  private final SelenideElement friends = $("li[role='menuitem'] a[href='/people/friends']");

  public EditSpendingPage editSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public void checkThatTableContains(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .should(visible);
  }
  public void checkMainPageOpened() {
    spendingTable.shouldBe(visible);
    headerBlock.shouldBe(visible);
  }
  public MainPage openMenuProfile() {
    headerBlock.shouldBe(visible);
    avatar.click();
    menu.shouldBe(visible);
    profile.click();
    return this;
  }
  public MainPage openFriends() {
    headerBlock.shouldBe(visible);
    avatar.click();
    menu.shouldBe(visible);
    friends.click();
    return this;
  }
}
