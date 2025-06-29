package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.openqa.selenium.By;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage {
    private final ElementsCollection activeCategories;
    private final ElementsCollection archivedCategories;
    private final SelenideElement archiveButton;
    private final SelenideElement restoreButton;
    private final SelenideElement showArchiveCategories;
    private final SelenideElement uploadPictureButton;
    private final SelenideElement avatar;
    public ProfilePage(SelenideDriver driver){
        this.activeCategories = driver.$$(By.xpath("//div[contains(@class, 'MuiChip-colorPrimary')]/parent::div"));
        this.archivedCategories = driver.$$(By.xpath("//div[contains(@class, 'MuiChip-colorDefault')]/parent::div"));
        this.archiveButton = driver.$x("//button[text()='Archive']");
        this.restoreButton = driver.$x("//button[text()='Unarchive']");
        this.showArchiveCategories = driver.$(By.xpath("//input[contains(@class, 'MuiSwitch-input')]"));
        this.uploadPictureButton = driver.$("input[type='file']");
        this.avatar = driver.$(".MuiAvatar-img");

    }
    public ProfilePage(){
        this.activeCategories = $$(By.xpath("//div[contains(@class, 'MuiChip-colorPrimary')]/parent::div"));
        this.archivedCategories = $$(By.xpath("//div[contains(@class, 'MuiChip-colorDefault')]/parent::div"));
        this.archiveButton = $x("//button[text()='Archive']");
        this.restoreButton = $x("//button[text()='Unarchive']");
        this.showArchiveCategories = $(By.xpath("//input[contains(@class, 'MuiSwitch-input')]"));
        this.uploadPictureButton = $("input[type='file']");
        this.avatar = $(".MuiAvatar-img");
    }


    public void checkActiveCategoryInList(String categoryName) {
        activeCategories.find(text(categoryName)).shouldBe(visible);
    }

    public void checkArchivedCategoryInList(String categoryName) {
        archivedCategories.find(text(categoryName)).shouldBe(visible);
    }

    public ProfilePage clickOnShowArchiveCategories() {
        showArchiveCategories.click();
        return this;
    }

    public ProfilePage archiveCategory(String categoryName) {
        activeCategories.filter(text(categoryName)).first().parent().$(".MuiIconButton-sizeMedium[aria-label='Archive category']").click();
        return this;
    }

    public ProfilePage confirmArchiveCategory() {
        archiveButton.click();
        return this;
    }

    public ProfilePage confirmActivateCategory() {
        restoreButton.click();
        return this;
    }

    public ProfilePage activateCategory(String categoryName) {
        archivedCategories.filter(text(categoryName)).first().parent().$(".MuiIconButton-sizeMedium[aria-label='Unarchive category']").click();
        return this;
    }
    public ProfilePage uploadPageForAvatar(String path) {
        uploadPictureButton.uploadFromClasspath(path);
        return this;
    }
    public ProfilePage checkAvatar(BufferedImage expected) throws IOException {
        Selenide.sleep(3000);
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
        return this;
    }
}
