package guru.qa.niffler.test.web;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {
  private static final Config CFG = Config.getInstance();
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
    Selenide.open(CFG.frontUrl(), LoginPage.class)
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
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password());
    Selenide.sleep(10000);
    BufferedImage actual = ImageIO.read(Objects.requireNonNull($("canvas[role='img']").screenshot()));

    assertFalse(new ScreenDiffResult(
            expected,
            actual
    ));
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
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .checkDiagramm(expected);
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
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .editSpending(user.testData().spendings().getFirst().description())
            .editAmount(newAmount)
            .shouldHaveStatSpend(statSpendList)
            .checkDiagramm(expected);
  }
}
