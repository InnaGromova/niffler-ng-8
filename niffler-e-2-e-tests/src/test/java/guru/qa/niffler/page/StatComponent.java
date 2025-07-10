package guru.qa.niffler.page;

import com.codeborne.selenide.*;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.condition.StatConditions;
import guru.qa.niffler.model.Bubble;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.color;
import static guru.qa.niffler.condition.StatConditions.statBubbles;


public class StatComponent {
    private final SelenideElement self = $("#stat");
    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = $("canvas[role='img']");

    @Nonnull
    public BufferedImage chartScreenShot() throws IOException {
       return ImageIO.read(Objects.requireNonNull(chart.screenshot()));
    }

    @Nonnull
    public StatComponent checkColor(Color... expectedColors) {
        bubbles.should(color(expectedColors));
        return this;
    }
    public StatComponent checkColorAndText (Bubble... expectedBubbles) {
        bubbles.should(statBubbles(expectedBubbles));
        return this;
    }
    public StatComponent checkBubblesInAnyOrder(Bubble... expectedBubbles) {
        bubbles.should(StatConditions.statBubblesInAnyOrder(expectedBubbles));
        return this;
    }
    public StatComponent checkBubblesContains(Bubble... expectedBubbles) {
        bubbles.should(StatConditions.statBubblesContains(expectedBubbles));
        return this;
    }
}
