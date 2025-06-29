package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Browser;
import guru.qa.niffler.config.Config;

import guru.qa.niffler.jupiter.*;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;



public class LoginTest {
    private static final Config CFG = Config.getInstance();
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    @Test
    public void checkSuccessfulAuthorization (){
        browserExtension.drivers().add(driver);
        String login  = "test-user1";
        String password = "test1";
        driver.open(CFG.frontUrl(), LoginPage.class);
        new LoginPage(driver)
                .doLogin(login, password)
                .checkMainPageOpened();
    }
    @ParameterizedTest
    @EnumSource(value = Browser.class, names = "FIREFOX")
    public void checkEnteredInvalidPassword(@ConvertWith(BrowserConverter.class) SelenideDriver driver){
        browserExtension.drivers().add(driver);
        String login  = "test-user1";
        String password = RandomDataUtils.randomUserPassword();

        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .doLogin(login, password);
        new LoginPage(driver).checkErrorMessageBlockVisible();
    }
}
