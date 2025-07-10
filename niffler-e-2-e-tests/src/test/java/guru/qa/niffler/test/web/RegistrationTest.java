package guru.qa.niffler.test.web;


import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomData;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
public class RegistrationTest {
    private static final Config CFG = Config.getInstance();
    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);
    @Test
    public void checkingAdditionNewUser (){
        String login = RandomData.randomUserName();
        String password = RandomData.randomUserPassword();

        driver.open(CFG.frontUrl(), LoginPage.class)
                .clickaddNewUserButton()
                .addNewUser(login, password, password )
                .checkingTheUserAddition();
    }
    @Test
    public void checkingImpossibleRegisterUserWithExistingLogin(){
        String login = "test-user1";
        String password = RandomData.randomUserPassword();
        String errorText = "Username `" + login + "` already exists";

        driver.open(CFG.frontUrl(), LoginPage.class)
                .clickaddNewUserButton()
                .addNewUser(login, password, password)
                .checkingErrorText(errorText);
    }

    @Test
    public void checkingImpossibleRegisterUserWithDifferentPasswordAndRepeatPassword(){
        String login = RandomData.randomUserName();
        String password = RandomData.randomUserPassword();
        String repeatPassword = RandomData.randomUserPassword();
        String errorText = "Passwords should be equal";

        driver.open(CFG.frontUrl(), LoginPage.class)
                .clickaddNewUserButton()
                .addNewUser(login, password, repeatPassword)
                .checkingErrorText(errorText);
    }
}
