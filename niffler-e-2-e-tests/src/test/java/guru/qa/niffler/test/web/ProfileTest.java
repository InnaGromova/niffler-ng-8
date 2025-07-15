package guru.qa.niffler.test.web;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
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
    @ApiLogin
    @Test
    void CheckingDisplayArchiveCategory(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();
        driver.open(ProfilePage.URL, ProfilePage.class)
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
    @ApiLogin
    @Test
    void activeCategoryShouldBePresentedInListAfterRestored(CategoryJson[] category) {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .clickOnShowArchiveCategories()
                .activateCategory(category[0].name())
                .confirmActivateCategory()
                .checkActiveCategoryInList(category[0].name());
    }
    @User
    @ApiLogin
    @ScreenShotTest(value = "img/avatar-expected.png", rewriteExpected = true)
    void checkAvatarTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .uploadPageForAvatar("img/avatar-upload.png")
                .checkAvatar(expected);
    }
    @Test
    @User
    @ApiLogin
    void editProfile(UserJson user) {
        String name = RandomData.randomUserName();
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setName(name)
                .clickSaveChangesButton()
                .checkAlertMessage("Profile successfully updated")
                .checkName(name);
    }
}
