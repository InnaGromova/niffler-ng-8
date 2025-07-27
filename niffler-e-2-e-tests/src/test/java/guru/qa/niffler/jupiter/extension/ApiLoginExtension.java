package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import java.util.List;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {
    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final UsersApiClient usersApiClient = new UsersApiClient();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser){
        this.setupBrowser = setupBrowser;
    }
    public ApiLoginExtension() {
        this(true);
    }
    public static ApiLoginExtension restApiLoginExtension() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {

                    final UserJson userToLogin;
                    final UserJson userFromUserExtention = UserExtension.createUser();

                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())){
                        if (userFromUserExtention == null){
                            throw new IllegalStateException("@User must be present in case that @ApiLogin is empty");
                        }
                        userToLogin = userFromUserExtention;
                    }else {
                        String apiLoginUsername = apiLogin.username();
                        List<CategoryJson> categories = spendApiClient.getAllCategories(apiLoginUsername, false);
                        List<SpendJson> spends = spendApiClient.getAllSpends(apiLoginUsername, null, null, null);
                        List<UserJson> friends = usersApiClient.getFriends(apiLoginUsername);
                        List<UserJson> outcomeInvitations = usersApiClient.getOutcomeInvitations(apiLoginUsername, null);
                        List<UserJson> incomeInvitations = usersApiClient.getIncomeInvitations(apiLoginUsername);


                        TestData testData = new TestData(
                                apiLogin.password(),
                                categories,
                                spends,
                                friends,
                                outcomeInvitations,
                                incomeInvitations);
                        UserJson fakeUser = new UserJson(
                                apiLoginUsername,
                                testData
                        );
                        if (userFromUserExtention != null){
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username and password");
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }


                    final String token = authApiClient.login(
                            userToLogin.username(),
                            userToLogin.testData().password()
                    );
                    setToken(token);
                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken());

                        WebDriverRunner.getWebDriver().manage().addCookie(
                                new Cookie(
                                        "JSESSIONID",
                                        ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
                                )
                        );
                        Selenide.open(MainPage.URL, MainPage.class).checkMainPageOpened();
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    public static void setToken(String token){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }
    public static String getToken(){
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }
    public static void setCode(String code){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }
    public static String getCode(){
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }
    public static Cookie getJsessionIdCookie(){
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}
