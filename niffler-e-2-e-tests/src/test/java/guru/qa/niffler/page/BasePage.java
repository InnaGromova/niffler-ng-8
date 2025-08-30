package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();
    private final SelenideElement alert = $(".MuiSnackbar-root");

    @Step("Check alert message with text")
    @Nonnull
    public T checkAlertMessage(String expectedText) {
        alert.should(Condition.visible).should(Condition.text(expectedText));
        return (T) this;
    }
    @Nonnull
    public <T extends BasePage<?>> T goToPage(String url, Class<T> pageClass) {
        return Selenide.open(url, pageClass);
    }
}
