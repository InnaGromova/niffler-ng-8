package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
public class RegistrationTest {
    private static final Config CFG = Config.getInstance();
    @Test
    public void checkingAdditionNewUser (){
        String login = RandomDataUtils.randomUserName();
        String password = RandomDataUtils.randomUserPassword();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickaddNewUserButton()
                .addNewUser(login, password, password )
                .checkingTheUserAddition();
    }
    @Test
    public void checkingImpossibleRegisterUserWithExistingLogin(){
        String login = "test-user1";
        String password = RandomDataUtils.randomUserPassword();
        String errorText = "Username `" + login + "` already exists";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickaddNewUserButton()
                .addNewUser(login, password, password)
                .checkingErrorText(errorText);
    }

    @Test
    public void checkingImpossibleRegisterUserWithDifferentPasswordAndRepeatPassword(){
        String login = RandomDataUtils.randomUserName();
        String password = RandomDataUtils.randomUserPassword();
        String repeatPassword = RandomDataUtils.randomUserPassword();
        String errorText = "Passwords should be equal";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickaddNewUserButton()
                .addNewUser(login, password, repeatPassword)
                .checkingErrorText(errorText);
    }
}
