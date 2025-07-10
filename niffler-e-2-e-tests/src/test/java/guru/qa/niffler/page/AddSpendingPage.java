package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public class AddSpendingPage extends BasePage {
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement submitButton = $("#save");
    private final SelenideElement amountInput = $("#amount");
    @Nonnull
    @Step("Enter  description")
    public AddSpendingPage enterDescription(String description) {
        descriptionInput.setValue(description);
        return this;
    }
    @Nonnull
    @Step("Enter category")
    public AddSpendingPage enterCategory(String category) {
        categoryInput.setValue(category);
        return this;
    }
    @Nonnull
    @Step("Enter amount")
    public AddSpendingPage enterAmount(String amount) {
        amountInput.setValue(amount);
        return this;
    }
    @Nonnull
    @Step("Save spending")
    public MainPage saveSpending() {
        submitButton.click();
        return new MainPage();
    }
}
