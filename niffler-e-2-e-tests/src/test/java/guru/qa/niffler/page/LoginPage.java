package guru.qa.niffler.page;


import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class LoginPage {

  private final SelenideElement usernameInput;
  private final SelenideElement passwordInput;
  private final SelenideElement submitBtn;
  private final SelenideElement addNewUserButton;
  private final SelenideElement errorMessageBlock;

  public LoginPage(SelenideDriver driver){
    this.usernameInput = driver.$("input[name='username']");
    this.passwordInput = driver.$("input[name='password']");
    this.submitBtn = driver.$("button[type='submit']");
    this.addNewUserButton = driver.$(".form__register");
    this.errorMessageBlock = driver.$(".form__error");
  }
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
