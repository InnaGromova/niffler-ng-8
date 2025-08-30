package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegistrationPage extends BasePage {

    private final SelenideElement registerForm = $("#register-form");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement repeatPasswordInput = $("input[name='passwordSubmit']");

    private final SelenideElement signUpButton = $("button[class='form__submit']");
    private final SelenideElement congratulationMessage = $(".form__paragraph_success");

    public RegistrationPage addNewUser (String username, String password, String confirmPassword) {
        usernameInput.shouldBe(visible).setValue(username);
        passwordInput.shouldBe(visible).setValue(password);
        repeatPasswordInput.shouldBe(visible).setValue(confirmPassword);
        signUpButton.click();
        return this;
    }

    public void checkingTheUserAddition () {
        congratulationMessage.shouldHave(text("Congratulations"));
    }
    public void checkingErrorText(String errorText) {
        registerForm.shouldHave(text(errorText));
    }
}
