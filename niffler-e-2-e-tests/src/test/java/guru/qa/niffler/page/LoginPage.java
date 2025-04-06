package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement addNewUserButton = $(".form__register");
  private final SelenideElement errorMessageBlock = $(".form__error");
  public MainPage doLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitBtn.click();
    return new MainPage();
  }
  public RegistrationPage clickaddNewUserButton() {
    addNewUserButton.click();
    return new RegistrationPage();
  }
  public void checkErrorMessageBlockVisible() {
    errorMessageBlock.shouldBe(visible);
  }
}
