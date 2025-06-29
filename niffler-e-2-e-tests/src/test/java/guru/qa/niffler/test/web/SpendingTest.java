package guru.qa.niffler.test.web;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.StatComponent;
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

    @User(
          spendings = @Spend(
                  category = "Цветы",
                  description = "Тест",
                  amount = 3000.00,
                  currency = CurrencyValues.RUB
          )
  )
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
    final String newDescription = "Подарок";
        driver.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .editSpending(user.testData().spendings().getFirst().description())
        .editDescription(newDescription);

    new MainPage().checkThatTableContains(newDescription);
  }
  @User(
          spendings = @Spend(
                  category = "Фитнес",
                  description = "Групповые занятия",
                  amount = 5000,
                  currency = CurrencyValues.RUB
          )
  )
  @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
  void checkStatComponent(UserJson user, BufferedImage expected) throws IOException {
    StatComponent statComponent = driver.open(CFG.frontUrl(), LoginPage.class)
              .doLogin(user.username(), user.testData().password())
              .getStatComponent();
      Selenide.sleep(10000);
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
                          currency = CurrencyValues.RUB
                  ),
                  @Spend(
                          category = "Видеосвет2",
                          description = "Профессиональный видеосвет штатив2",
                          amount = 3000.00,
                          currency = CurrencyValues.RUB
                  )
          }

  )
  @ScreenShotTest(value = "img/2spend-expected.png", rewriteExpected = true)
  void checkStatComponent2SpendTest(UserJson user, BufferedImage expected) throws IOException {
      StatComponent statComponent = driver.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .checkDiagramm(expected)
                    .getStatComponent();
      Selenide.sleep(10000);
    statComponent.checkBubblesInAnyOrder(new Bubble(Color.green, "Освещение2 2000 ₽"),new Bubble(Color.yellow, "Видеосвет2 3000 ₽") );
  }
  @User(
          spendings = {
                  @Spend(
                          category = "Освещение",
                          description = "Студийный свет",
                          amount = 2000.00,
                          currency = CurrencyValues.RUB
                  ),
                  @Spend(
                          category = "Видеосвет",
                          description = "Профессиональный видеосвет штатив",
                          amount = 3000.00,
                          currency = CurrencyValues.RUB
                  )
          }

  )
  @ScreenShotTest(value = "img/editSpend-expected.png", rewriteExpected = true)
  void checkStatComponentEditSpendTest(UserJson user, BufferedImage expected) throws IOException {
    final String newAmount = "4000";
    final List<String> statSpendList = List.of(
            "Освещение 4000 ₽",
            "Видеосвет 3000 ₽"
    );
      driver.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
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
                          currency = CurrencyValues.RUB
                  ),
                  @Spend(
                          category = "Плаванье",
                          description = "Очки",
                          amount = 1000.00,
                          currency = CurrencyValues.RUB
                  ),
                  @Spend(
                          category = "Бег",
                          description = "Костюм",
                          amount = 2000.00,
                          currency = CurrencyValues.RUB
                  )
          }

  )
  @Test
  void statBubblesContains(UserJson user){
    StatComponent statComponent = driver.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
                    .getStatComponent();
      Selenide.sleep(10000);
    statComponent.checkBubblesContains(new Bubble(Color.green, "Йога 2000 ₽"),
            new Bubble(Color.orange, "Плаванье 1000 ₽"));
  }
    @User(
            spendings = {
                    @Spend(
                            category = "Рисование",
                            description = "Холст",
                            amount = 2000.00,
                            currency = CurrencyValues.RUB
                    ),
                    @Spend(
                            category = "Продукты",
                            description = "Йогурт",
                            amount = 100.00,
                            currency = CurrencyValues.RUB
                    ),
                    @Spend(
                            category = "Развлечения",
                            description = "Кино",
                            amount = 1000.00,
                            currency = CurrencyValues.RUB
                    )
            }

    )
    @Test
    void spendTableTest(UserJson user){
        driver.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkSpendsTable(user.testData().spendings().toArray(SpendJson[]::new));
    }
}
