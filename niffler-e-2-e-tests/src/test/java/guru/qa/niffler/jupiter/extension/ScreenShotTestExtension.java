package guru.qa.niffler.jupiter.extension;

import ch.qos.logback.classic.Logger;
import com.codeborne.selenide.WebDriverThreadLocalContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDif;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.WebDriver;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);
    private static Logger log;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ScreenShotTest screenShotTest = extensionContext.getRequiredTestMethod().getAnnotation(ScreenShotTest.class);
        return ImageIO.read(new ClassPathResource(screenShotTest.value()).getInputStream());
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ScreenDif screenDif = new ScreenDif(
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getExpected())),
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getActual())),
                "data:image/png;base64," + Base64.getEncoder().encodeToString(imageToBytes(getDif()))
        );

        Allure.addAttachment("Screenshot diff",
                "application/vnd.allure.image.diff",
                objectMapper.writeValueAsString(screenDif)
        );
        throw throwable;
    }

    public static void setExpected(BufferedImage expected){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }
    public static BufferedImage getExpected(){
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }
    public static void setActual(BufferedImage actual){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
    }
    public static BufferedImage getActual(){
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }
    public static void setDif(BufferedImage dif){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", dif);
    }
    public static BufferedImage getDif(){
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }
    private static byte[] imageToBytes(BufferedImage image){
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
