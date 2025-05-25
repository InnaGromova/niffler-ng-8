package guru.qa.niffler.test.web;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}
