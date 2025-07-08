package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
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

public class MainPage {

  private final StatComponent statComponent = new StatComponent();
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

//  public MainPage(SelenideDriver driver){
//    this.tableRows = driver.$$("#spendings tbody tr");
//    this.spendingTable = driver.$("#spendings");
//    this.headerBlock = driver.$("#root header");
//    this.menu = driver.$("ul[role='menu']");
//    this.avatar = driver.$("svg[data-testid='PersonIcon']");
//    this.profile = driver.$("li[role='menuitem'] a[href='/profile']");
//    this.friends = driver.$("li[role='menuitem'] a[href='/people/friends']");
//    this.allPeople = driver.$("li[role='menuitem'] a[href='/people/all']");
//    this.searchField = driver.$("input[placeholder='Search']");
//    this.diagram = driver.$("canvas[role='img']");
//    this.statCategories = driver.$$("#legend-container li");
//  }
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

  public StatComponent getStatComponent() {
    return statComponent;
  }
  public SpendTable getSpendTable() {
    return spendTable;
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
    Selenide.sleep(10000);
    addNewSpendingButton.click();
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
