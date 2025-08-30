package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent {
    private final SelenideElement self = $("input[aria-label='search']");
    private final SelenideElement searchInput = $("input[aria-label='search']");
    private final SelenideElement searchClear = $("//button[@id='input-clear']");

    public SearchField(Object currentPage, SelenideElement self) {
        super(currentPage, self);
    }

    @Nonnull
    @Step("Search Request")
    public SearchField search (String query) {
        searchInput.sendKeys(query);
        searchInput.pressEnter();
        return this;
    }
    @Nonnull
    @Step("Clear search")
    public SearchField clearIfNotEmpty () {
        if (searchClear.isDisplayed())
            searchClear.click();
        return this;
    }
}
