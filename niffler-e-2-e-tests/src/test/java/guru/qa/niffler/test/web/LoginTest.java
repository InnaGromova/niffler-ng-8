package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

public class LoginTest {
    private static final Config CFG = Config.getInstance();
    LoginPage loginPage = new LoginPage();
    @Test
    public void checkSuccessfulAuthorization (){
        String login  = "test-user1";
        String password = "test1";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(login, password)
                .checkMainPageOpened();
    }
    @Test
    public void checkEnteredInvalidPassword(){
        String login  = "test-user1";
        String password = RandomDataUtils.randomUserPassword();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(login, password);
        loginPage.checkErrorMessageBlockVisible();
    }
}
