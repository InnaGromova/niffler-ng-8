package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<P, C extends BaseComponent<P, C>> {
    @Getter
    protected final SelenideElement self;
    protected final P page;
    public BaseComponent(P currentPage, SelenideElement self) {
        this.self = self;
        this.page = currentPage;
    }
    public P returnToPage() {
        return page;
    }
}
