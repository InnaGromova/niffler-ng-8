package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
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
public class Calendar {
    private SelenideElement self = $(".MuiDateCalendar-root");
    private final SelenideElement chooseDateButton = $("button[aria-label*=\"Choose date\"]");
    private final SelenideElement currentMonthAndYear = self.$(".MuiPickersFadeTransitionGroup-root");
    private final ElementsCollection selectYear = self.$$(".MuiPickersYear-yearButton");
    private final SelenideElement previousMonthButton = self.$("[data-testid=\"ArrowLeftIcon\"]");
    private final SelenideElement nextMonthButton = self.$("[data-testid=\"ArrowRightIcon\"]");
    private final ElementsCollection daysInMonth = $$(".MuiPickersSlideTransition-root button");
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
