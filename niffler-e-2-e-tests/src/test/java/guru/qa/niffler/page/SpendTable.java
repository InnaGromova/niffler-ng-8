package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.BaseComponent;
import guru.qa.niffler.values.DataFilterValues;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SpendTable extends BasePage<SpendTable> {
    @ParametersAreNonnullByDefault
    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement inputPeriod = $("//input[@name='period']");
    private final String periodOptions = "li[data-value='%s']";
    private final ElementsCollection spendTableRows = $$("#spendings tbody tr");
    private final SelenideElement searchField = $("input[placeholder='Search']");
    private final SelenideElement deleteButton = $("//button[@id='delete']");
    private final SelenideElement confirmWindowDeleteButton = $("//button[text()='Delete']");

    public ElementsCollection getTableRows() {
        return tableRows;
    }
    @Nonnull
    @Step("Select period")
    public SpendTable selectPeriod(DataFilterValues period){
        inputPeriod.click();
        $(periodOptions.formatted(period)).click();
        return this;
    }
    @Nonnull
    @Step("Edit spend")
    public EditSpendingPage editSpending(String spendingDescription) {
        findSpending(spendingDescription);
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }
    @Nonnull
    @Step("Find spend by description")
    public void findSpending(String spendingDescription) {
        searchField.setValue(spendingDescription).pressEnter();
    }
    @Nonnull
    @Step("Delete spending by description")
    public SpendTable deleteSpending(String description){
        findSpending(description);
        deleteButton.click();
        confirmWindowDeleteButton.click();
        return this;
    }
    @Nonnull
    @Step("Сheck spending table contains")
    public SpendTable checkTableContains(String... expectedSpends){
        for (String expectedSpend : expectedSpends) {
            findSpending(expectedSpend);
            tableRows.find(text(expectedSpend)).should(visible);
        }
        return this;
    }
    @Nonnull
    @Step("Сheck spending table contains by description")
    public SpendTable checkTableContainsByDescription(String description){
            tableRows.find(text(description)).should(visible);
        return this;
    }
    @Nonnull
    @Step("Сheck  table size")
    public SpendTable checkTableSize(int expectedSize) {
        spendTableRows.shouldHave(size(expectedSize));
        return this;
    }

}
