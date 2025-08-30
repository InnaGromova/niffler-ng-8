package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.Bubble;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class StatConditions {

    public static WebElementsCondition statBubbles(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expectedDescription = Arrays.stream(expectedBubbles)
                    .map(bubble -> String.format("{color: %s, text: '%s'}", bubble.color(), bubble.text()))
                    .collect(Collectors.toList()).toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format(
                            "List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size()
                    );
                    return rejected(message, elements);
                }

                boolean passed = true;
                List<String> actualDescriptions = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Bubble bubbleToCheck = expectedBubbles[i];

                    final String actualColor = elementToCheck.getCssValue("background-color");
                    final boolean colorMatch = bubbleToCheck.color().rgb.equals(actualColor);

                    final String actualText = elementToCheck.getText();
                    final boolean textMatch = bubbleToCheck.text().equals(actualText);

                    final String actualDescription = String.format(
                            "{color: %s, text: '%s'}", actualColor, actualText
                    );
                    actualDescriptions.add(actualDescription);

                    if (passed) {
                        passed = colorMatch && textMatch;
                    }
                }

                if (!passed) {
                    final String actualDescription = actualDescriptions.toString();
                    final String message = String.format(
                            "List mismatch (expected: %s, actual: %s)", expectedDescription, actualDescription
                    );
                    return rejected(message, actualDescriptions);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedDescription;
            }
        };
    }
    public static WebElementsCondition color(Color... expectedColor){
        return new WebElementsCondition() {
            private final String expectedRgba = Arrays.stream(expectedColor).map(c -> c.rgb).toList().toString();
            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColor)) {
                    throw new IllegalArgumentException("No expected colors given");
                }
                if (expectedColor.length != elements.size()) {
                    final String message = String.format("List size mismatch (expexted: %s, actual: %s)", expectedColor.length, elements.size());
                    return rejected(message, elements);
                }
                boolean passed = true;
                List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColor[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba);
                    }
                }
                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format(
                            "List colors mismatch (expexted: %s, actual: %s)", expectedRgba, actualRgba
                    );
                    return rejected(message, actualRgba);
                }
                return accepted();
            }
            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }
    public static WebElementsCondition statBubblesInAnyOrder(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final List<Bubble> expectedBubblesList = Arrays.asList(expectedBubbles);
            private final String expectedDescription = expectedBubblesList.stream()
                    .map(bubble -> String.format("{color: %s, text: '%s'}", bubble.color().rgb, bubble.text()))
                    .collect(Collectors.joining(", ", "[", "]"));

            @Nonnull
            @CheckReturnValue
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (expectedBubbles.length != elements.size()) {
                    String message = String.format(
                            "List size mismatch (expected: %s, actual: %s)",
                            expectedBubbles.length, elements.size()
                    );
                    return rejected(message, elements);
                }
                List<Bubble> remainingExpectedBubbles = new ArrayList<>(expectedBubblesList);

                for (WebElement element : elements) {
                    String actualColor = element.getCssValue("background-color");
                    String actualText = element.getText();

                    boolean matchFound = false;
                    for (int i = 0; i < remainingExpectedBubbles.size(); i++) {
                        Bubble expectedBubble = remainingExpectedBubbles.get(i);
                        if (expectedBubble.color().rgb.equals(actualColor) && expectedBubble.text().equals(actualText)) {
                            // Удаляем соответствующий ожидаемый пузырёк
                            remainingExpectedBubbles.remove(i);
                            matchFound = true;
                            break;
                        }
                    }

                    if (!matchFound) {
                        String actualDescription = String.format(
                                "{color: %s, text: '%s'}",
                                actualColor, actualText
                        );
                        String message = String.format(
                                "Unexpected bubble found: %s",
                                actualDescription
                        );
                        return rejected(message, elements);
                    }
                }
                if (!remainingExpectedBubbles.isEmpty()) {
                    String missingBubbles = remainingExpectedBubbles.stream()
                            .map(bubble -> String.format("{color: %s, text: '%s'}", bubble.color().rgb, bubble.text()))
                            .collect(Collectors.joining(", ", "[", "]"));
                    String message = String.format(
                            "Missing expected bubbles: %s",
                            missingBubbles
                    );
                    return rejected(message, elements);
                }

                return accepted();
            }

            @Override
            public String toString() {
                return expectedDescription;
            }
        };
    }
    public static WebElementsCondition statBubblesContains(@Nonnull Bubble... bubbles) {
        return new WebElementsCondition() {
            private final String[] expectedColors = Arrays.stream(bubbles).map(bubble -> bubble.color().rgb).toArray(String[]::new);
            private final String[] expectedTexts = Arrays.stream(bubbles).map(Bubble::text).toArray(String[]::new);

            @Nonnull
            @CheckReturnValue
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {

                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (bubbles.length > elements.size()) {
                    String message = String.format("List size is more than expected (expected: %s, actual: %s)",
                            bubbles.length, elements.size());
                    return rejected(message, elements);
                }
                boolean passed = true;
                HashMap<String, String> actualBubbles = actualBubbles(elements);

                for (Bubble bubble : bubbles) {
                    if (passed) {
                        passed = actualBubbles.containsValue(bubble.text()) &&
                                actualBubbles.get(bubble.color().rgb).equals(bubble.text());
                    }
                }

                if (!passed) {
                    final String actualRgba = actualBubbles.keySet().toString();
                    final String actualText = actualBubbles.values().toString();
                    String message1 = String.format("Bubble mismatch (expected color: %s, actual color: %s \n" +
                                    "expected text: %s, actual text: %s",
                            expectedColors, actualRgba, expectedTexts, actualText
                    );
                    return rejected(message1, "Text: " + actualText +
                            "\nColor: " + actualRgba);


                }
                return

                        accepted();
            }

            @Override
            public String toString() {
                return "Text: " + Arrays.toString(expectedTexts) +
                        "\nColor:" + Arrays.toString(expectedColors);
            }
        };
    }

    private static HashMap<String, String> actualBubbles(List<WebElement> elements) {
        HashMap<String, String> actualBubbles = new HashMap<>();

        for (int i = 0; i < elements.size(); i++) {
            final WebElement elementToCheck = elements.get(i);
            final String rgba = elementToCheck.getCssValue("background-color");
            final String text = elementToCheck.getText();
            actualBubbles.put(rgba, text);
        }
        return actualBubbles;
    }
}
