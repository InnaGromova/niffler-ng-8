package guru.qa.niffler.jupiter;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Browser;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof Browser)) {
            throw new ArgumentConversionException("Expected Browser enum value");
        }
        Browser browser = (Browser) source;
        switch (browser) {
            case CHROME:
                Configuration.browser = "chrome";
                return new SelenideDriver(browser.getConfig());
            case FIREFOX:
                Configuration.browser = "firefox";
                return new SelenideDriver(browser.getConfig());
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }
}
