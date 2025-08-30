package guru.qa.niffler.test.web;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.Currency;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.*;
import guru.qa.niffler.utils.RandomData;
import guru.qa.niffler.utils.ScreenDiffResult;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {
  private static final Config CFG = Config.getInstance();
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);
    private StatComponent statComponent;
    private final SpendTable spendTable = new SpendTable();
    @User(
          spendings = @Spend(
                  category = "Цветы",
                  description = "Тест",
                  amount = 3000.00,
                  currency = Currency.RUB
          )
  )
    @Test
    @ApiLogin
    void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
    final String newDescription = "Подарок";
        Selenide.open(MainPage.URL, MainPage.class);
        spendTable.editSpending(
                user
                        .testData()
                        .spendings()
                        .getFirst().
                        description()
                )
        .editDescription(newDescription);
    new MainPage().checkThatTableContains(newDescription);
  }
  @User(
          spendings = @Spend(
                  category = "Фитнес",
                  description = "Групповые занятия",
                  amount = 5000,
                  currency = Currency.RUB
          )
  )
  @ApiLogin
  @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
  void checkStatComponent(UserJson user, BufferedImage expected) throws IOException {
    StatComponent statComponent = Selenide.open(MainPage.URL, MainPage.class).getStatComponent();
    assertFalse(new ScreenDiffResult(
            expected,
            statComponent.chartScreenShot()
    ),
    "Screen comparasion failure");
    statComponent.checkColorAndText(new Bubble(Color.yellow, "Фитнес 5000 ₽"));
  }

  @User(
          spendings = {
                  @Spend(
                          category = "Освещение2",
                          description = "Студийный свет2",
                          amount = 2000.00,
                          currency = Currency.RUB
                  ),
                  @Spend(
                          category = "Видеосвет2",
                          description = "Профессиональный видеосвет штатив2",
                          amount = 3000.00,
                          currency = Currency.RUB
                  )
          }

  )
  @ApiLogin
  @ScreenShotTest(value = "img/2spend-expected.png", rewriteExpected = true)
  void checkStatComponent2SpendTest(UserJson user, BufferedImage expected)  {
      StatComponent statComponent = Selenide.open(MainPage.URL, MainPage.class).getStatComponent();
    statComponent.checkBubblesInAnyOrder(new Bubble(Color.green, "Освещение2 2000 ₽"),new Bubble(Color.yellow, "Видеосвет2 3000 ₽") );
  }
  @User(
          spendings = {
                  @Spend(
                          category = "Освещение",
                          description = "Студийный свет",
                          amount = 2000.00,
                          currency = Currency.RUB
                  ),
                  @Spend(
                          category = "Видеосвет",
                          description = "Профессиональный видеосвет штатив",
                          amount = 3000.00,
                          currency = Currency.RUB
                  )
          }

  )
  @ApiLogin
  @ScreenShotTest(value = "img/editSpend-expected.png", rewriteExpected = true)
  void checkStatComponentEditSpendTest(UserJson user, BufferedImage expected) throws IOException {
    final String newAmount = "4000";
    final List<String> statSpendList = List.of(
            "Освещение 4000 ₽",
            "Видеосвет 3000 ₽"
    );
      Selenide.open(MainPage.URL, MainPage.class);
      spendTable
            .editSpending(user.testData().spendings().getFirst().description())
            .editAmount(newAmount)
            .shouldHaveStatSpend(statSpendList)
            .checkDiagramm(expected);
  }
  @User(
          spendings = {
                  @Spend(
                          category = "Йога",
                          description = "Абонемент",
                          amount = 2000.00,
                          currency = Currency.RUB
                  ),
                  @Spend(
                          category = "Плаванье",
                          description = "Очки",
                          amount = 1000.00,
                          currency = Currency.RUB
                  ),
                  @Spend(
                          category = "Бег",
                          description = "Костюм",
                          amount = 2000.00,
                          currency = Currency.RUB
                  )
          }

  )
  @ApiLogin
  @Test
  void statBubblesContains(UserJson user){
    StatComponent statComponent = Selenide.open(MainPage.URL, MainPage.class).getStatComponent();
    statComponent.checkBubblesContains(new Bubble(Color.green, "Йога 2000 ₽"),
            new Bubble(Color.orange, "Плаванье 1000 ₽"));
  }
    @User(
            spendings = {
                    @Spend(
                            category = "Рисование",
                            description = "Холст",
                            amount = 2000.00,
                            currency = Currency.RUB
                    ),
                    @Spend(
                            category = "Продукты",
                            description = "Йогурт",
                            amount = 100.00,
                            currency = Currency.RUB
                    ),
                    @Spend(
                            category = "Развлечения",
                            description = "Кино",
                            amount = 1000.00,
                            currency = Currency.RUB
                    )
            }

    )
    @ApiLogin
    @Test
    void spendTableTest(UserJson user){
        Selenide.open(MainPage.URL,MainPage.class)
                .checkSpendsTable(user.testData().spendings().toArray(SpendJson[]::new));
    }
    @Test
    @ApiLogin(username = "test-user4", password = "test4")
    void addNewSpending() {
        String description = RandomData.randomSentence(1);
        String categotyName = RandomData.randomCategoryName();
        String amount = "1000";
        Selenide.open(AddSpendingPage.URL, AddSpendingPage.class)
                .enterAmount(amount)
                .enterCategory(categotyName)
                .enterDescription(description)
                .saveSpending()
                .checkAlertMessage("New spending is successfully created")
                .getSpendTable()
                .checkTableContainsByDescription(description)
                .checkTableSize(1);
    }
}
