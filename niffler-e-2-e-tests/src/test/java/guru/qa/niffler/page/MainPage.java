package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.condition.SpendConditions;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
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

public class MainPage extends BasePage<MainPage> {
  private final ElementsCollection tableRows;
  private final SelenideElement spendingTable;
  private final SelenideElement headerBlock;
  private final SelenideElement avatar;
  private final SelenideElement searchField;
  private final SelenideElement diagram;
  private final SelenideElement addNewSpendingButton;
  private final ElementsCollection statCategories;
  private final ElementsCollection menuItems;
  private SpendTable spendTable = new SpendTable();
  private StatComponent statComponent = new StatComponent();

  public MainPage(){
    this.tableRows = $$("#spendings tbody tr");
    this.spendingTable = $("#spendings");
    this.headerBlock = $("#root header");
    this.avatar = $("svg[data-testid='PersonIcon']");
    this.searchField = $("input[placeholder='Search']");
    this.diagram = $("canvas[role='img']");
    this.statCategories = $$("#legend-container li");
    this.addNewSpendingButton = headerBlock.$("a[href='/spending']");
    this.menuItems = headerBlock.parent().parent().$$("[role='menuitem']");
  }
  public SpendTable getSpendTable() {
    return spendTable;
  }
  public StatComponent getStatComponent() {
    return statComponent;
  }
  @Nonnull
  @Step("Check contents of table with spends")
  public void checkThatTableContains(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .should(visible);
  }
  @Nonnull
  @Step("Check main page open")
  public void checkMainPageOpened() {
    spendingTable.shouldBe(visible);
    headerBlock.shouldBe(visible);
  }
  @Nonnull
  @Step("Go to Profile")
  public ProfilePage openMenuProfile() {
    avatar.click();
    menuItems.findBy(text("Profile")).click();
    return new ProfilePage();
  }
  @Nonnull
  @Step("Go to Friends")
  public FriendsPage openFriends() {
    avatar.click();
    menuItems.findBy(text("Friends")).click();
    return new FriendsPage();
  }
  @Nonnull
  @Step("Go to All People")
  public AllPeoplePage openMenuAllPeople() {
    avatar.click();
    menuItems.findBy(text("All People")).click();
    return new AllPeoplePage();
  }
  @Nonnull
  @Step("Go to All People")
  public AddSpendingPage openAddSpendingPage() {
    addNewSpendingButton.shouldBe(visible).click();
    return new AddSpendingPage();
  }
  @Nonnull
  @Step("Sign Out")
  public LoginPage signOut() {
    avatar.click();
    menuItems.findBy(text("Sign Out")).click();
    return new LoginPage();
  }
  @Nonnull
  @Step("Checking Diagramm")
  public MainPage checkDiagramm(BufferedImage expected) throws IOException {
    Selenide.sleep(10000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull(diagram.screenshot()));
    assertFalse(new ScreenDiffResult(
            expected,
            actual
    ));
    return this;
  }
  @Nonnull
  @Step("Checking Categories")
  public MainPage shouldHaveStatSpend(List<String> expectedCategories) {
    statCategories.shouldHave(CollectionCondition.texts(expectedCategories));
    return this;
  }
  @Nonnull
  @Step("Checking Table with Spends")
  public MainPage checkSpendsTable(SpendJson... expectedSpends) {
    Selenide.sleep(10000);
    spendTable.getTableRows().should(SpendConditions.spends(expectedSpends));
    return this;
  }
}
