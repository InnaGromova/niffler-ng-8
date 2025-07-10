package guru.qa.niffler.test.web;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.SelenideUtils;
import guru.qa.niffler.utils.RandomData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static com.codeborne.selenide.Selenide.open;

import java.awt.image.BufferedImage;
import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);
    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void CheckingDisplayArchiveCategory(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();
        driver.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openMenuProfile();
        new ProfilePage()
                .archiveCategory(category.name())
                .confirmArchiveCategory()
                .clickOnShowArchiveCategories()
                .checkArchivedCategoryInList(category.name());
    }
    @User(
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void activeCategoryShouldBePresentedInListAfterRestored(CategoryJson[] category) {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("test-user1", "test1")
                .openMenuProfile();
        new ProfilePage()
                .clickOnShowArchiveCategories()
                .activateCategory(category[0].name())
                .confirmActivateCategory()
                .checkActiveCategoryInList(category[0].name());
    }
    @User
    @ScreenShotTest(value = "img/avatar-expected.png", rewriteExpected = true)
    void checkAvatarTest(UserJson user, BufferedImage expected) throws IOException {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openMenuProfile();
        new ProfilePage()
                .uploadPageForAvatar("img/avatar-upload.png")
                .checkAvatar(expected);
    }
    @Test
    @User
    void editProfile(UserJson user) {
        String name = RandomData.randomUserName();
        open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .openMenuProfile()
                .setName(name)
                .clickSaveChangesButton()
                .checkAlertMessage("Profile successfully updated")
                .checkName(name);
    }
}
