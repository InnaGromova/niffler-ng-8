package guru.qa.niffler.test.web;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    @Category(
            username = "test-user1",
            archived = false
    )
    @Test
    void CheckingDisplayArchiveCategory(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("test-user1", "test1")
                .openMenuProfile();
        new ProfilePage()
                .archiveCategory(category.name())
                .confirmArchiveCategory()
                .clickOnShowArchiveCategories()
                .checkArchivedCategoryInList(category.name());
    }

    @Category(
            username = "test-user1",
            archived = true
    )
    @Test
    void activeCategoryShouldBePresentedInListAfterRestored(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("test-user1", "test1")
                .openMenuProfile();
        new ProfilePage()
                .clickOnShowArchiveCategories()
                .activateCategory(category.name())
                .confirmActivateCategory()
                .checkActiveCategoryInList(category.name());
    }
}
