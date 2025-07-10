package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage extends BasePage <ProfilePage>{
    private final ElementsCollection activeCategories;
    private final ElementsCollection archivedCategories;
    private final SelenideElement archiveButton;
    private final SelenideElement restoreButton;
    private final SelenideElement showArchiveCategories;
    private final SelenideElement uploadPictureButton;
    private final SelenideElement avatar;
    private final SelenideElement nameInput;
    private final SelenideElement saveChangeButton;

//    public ProfilePage(SelenideDriver driver){
//        this.activeCategories = driver.$$(By.xpath("//div[contains(@class, 'MuiChip-colorPrimary')]/parent::div"));
//        this.archivedCategories = driver.$$(By.xpath("//div[contains(@class, 'MuiChip-colorDefault')]/parent::div"));
//        this.archiveButton = driver.$x("//button[text()='Archive']");
//        this.restoreButton = driver.$x("//button[text()='Unarchive']");
//        this.showArchiveCategories = driver.$(By.xpath("//input[contains(@class, 'MuiSwitch-input')]"));
//        this.uploadPictureButton = driver.$("input[type='file']");
//        this.avatar = driver.$(".MuiAvatar-img");
//
//    }
    public ProfilePage(){
        this.activeCategories = $$(By.xpath("//div[contains(@class, 'MuiChip-colorPrimary')]/parent::div"));
        this.archivedCategories = $$(By.xpath("//div[contains(@class, 'MuiChip-colorDefault')]/parent::div"));
        this.archiveButton = $x("//button[text()='Archive']");
        this.restoreButton = $x("//button[text()='Unarchive']");
        this.showArchiveCategories = $(By.xpath("//input[contains(@class, 'MuiSwitch-input')]"));
        this.uploadPictureButton = $("input[type='file']");
        this.avatar = $(".MuiAvatar-img");
        this.nameInput = $("#name");
        this.saveChangeButton = $("[type='submit']");
    }
    @Nonnull
    @Step("Checking the list of active categories")
    public void checkActiveCategoryInList(String categoryName) {
        activeCategories.find(text(categoryName)).shouldBe(visible);
    }
    @Nonnull
    @Step("Checking the list of archived categories")
    public void checkArchivedCategoryInList(String categoryName) {
        archivedCategories.find(text(categoryName)).shouldBe(visible);
    }
    @Nonnull
    @Step("To view archived categories")
    public ProfilePage clickOnShowArchiveCategories() {
        showArchiveCategories.click();
        return this;
    }
    @Nonnull
    @Step("Archive category")
    public ProfilePage archiveCategory(String categoryName) {
        activeCategories.filter(text(categoryName)).first().parent().$(".MuiIconButton-sizeMedium[aria-label='Archive category']").click();
        return this;
    }
    @Nonnull
    @Step("Confirm archive  category")
    public ProfilePage confirmArchiveCategory() {
        archiveButton.click();
        return this;
    }
    @Nonnull
    @Step("Confirm activate  category")
    public ProfilePage confirmActivateCategory() {
        restoreButton.click();
        return this;
    }
    @Nonnull
    @Step("Activating a category")
    public ProfilePage activateCategory(String categoryName) {
        archivedCategories.filter(text(categoryName)).first().parent().$(".MuiIconButton-sizeMedium[aria-label='Unarchive category']").click();
        return this;
    }
    @Nonnull
    @Step("Uploading an avatar")
    public ProfilePage uploadPageForAvatar(String path) {
        uploadPictureButton.uploadFromClasspath(path);
        return this;
    }
    @Nonnull
    @Step("Checking the avatar")
    public ProfilePage checkAvatar(BufferedImage expected) throws IOException {
        Selenide.sleep(3000);
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
        return this;
    }
    @Nonnull
    @Step("Enter the user's name")
    public ProfilePage setName(String name) {
        nameInput.sendKeys(name);
        return this;
    }
    @Nonnull
    @Step("Click on the save changes button")
    public ProfilePage clickSaveChangesButton() {
        saveChangeButton.click();
        Selenide.sleep(1000);
        return this;
    }
    @Nonnull
    @Step("Check username")
    public ProfilePage checkName(String username) {
        Selenide.refresh();
        nameInput.shouldHave(value(username));
        return this;
    }
}
