package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.BaseComponent;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.util.Calendar.*;

@ParametersAreNonnullByDefault
public class Calendar extends BaseComponent{
    private final SelenideElement chooseDateButton = $("button[aria-label*=\"Choose date\"]");
    private final SelenideElement currentMonthAndYear = self.$(".MuiPickersFadeTransitionGroup-root");
    private final ElementsCollection selectYear = self.$$(".MuiPickersYear-yearButton");
    private final SelenideElement previousMonthButton = self.$("[data-testid=\"ArrowLeftIcon\"]");
    private final SelenideElement nextMonthButton = self.$("[data-testid=\"ArrowRightIcon\"]");
    private final ElementsCollection daysInMonth = $$(".MuiPickersSlideTransition-root button");

    public Calendar(Object currentPage, SelenideElement self) {
        super(currentPage, $("div.MuiInputBase-root input[name=\"date\"]"));
    }

    @Nonnull
    @Step("Select date in calender")
    public void selectDateInCalendar(Date date) {
        java.util.Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        chooseDateButton.click();
        currentMonthAndYear.click();
        selectYear.findBy(text(String.valueOf(cal.get(YEAR)))).click();

        int monthIndex = cal.get(MONTH);
        int currentMonthIndex = Month.valueOf(currentMonthAndYear.getText().split(" ")[0].toUpperCase()).ordinal();
        while (monthIndex < currentMonthIndex) {
            previousMonthButton.click();
            currentMonthIndex = Month.valueOf(currentMonthAndYear.getText().split(" ")[0].toUpperCase()).ordinal();
        }
        while (currentMonthIndex < monthIndex) {
            nextMonthButton.click();
            currentMonthIndex = Month.valueOf(currentMonthAndYear.getText().split(" ")[0].toUpperCase()).ordinal();
        }

        daysInMonth.findBy(text(String.valueOf(cal.get(DAY_OF_MONTH)))).click();
    }
}
