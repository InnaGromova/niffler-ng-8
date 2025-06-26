package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendConditions;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage {

  private final StatComponent statComponent = new StatComponent();
  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement spendingTable = $("#spendings");
  private final SelenideElement headerBlock = $("#root header");
  private final SelenideElement menu = $("ul[role='menu']");
  private final SelenideElement avatar = $("svg[data-testid='PersonIcon']");
  private final SelenideElement profile = $("li[role='menuitem'] a[href='/profile']");
  private final SelenideElement friends = $("li[role='menuitem'] a[href='/people/friends']");
  private final SelenideElement searchField = $("input[placeholder='Search']");
  private final SelenideElement diagram = $("canvas[role='img']");
  private final ElementsCollection statCategories = $$("#legend-container li");
  private SpendTable spendTable = new SpendTable();

  public StatComponent getStatComponent() {
    return statComponent;
  }
  public EditSpendingPage editSpending(String spendingDescription) {
    findSpending(spendingDescription);
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }
  public void findSpending(String spendingDescription) {
    searchField.setValue(spendingDescription).pressEnter();
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
    Selenide.sleep(10000);
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
  public MainPage checkDiagramm(BufferedImage expected) throws IOException {
    Selenide.sleep(10000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull(diagram.screenshot()));
    assertFalse(new ScreenDiffResult(
            expected,
            actual
    ));
    return this;
  }
  public MainPage shouldHaveStatSpend(List<String> expectedCategories) {
    statCategories.shouldHave(CollectionCondition.texts(expectedCategories));
    return this;
  }
  public MainPage checkSpendsTable(SpendJson... expectedSpends) {
    Selenide.sleep(10000);
    spendTable.getTableRows().should(SpendConditions.spends(expectedSpends));
    return this;
  }
}
