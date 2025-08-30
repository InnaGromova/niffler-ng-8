package guru.qa.niffler.utils;

import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;

import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

public class ScreenDiffResult implements BooleanSupplier {
    private final BufferedImage expected;
    private final BufferedImage actual;
    private final boolean hasDif;

    public ScreenDiffResult(BufferedImage expected, BufferedImage actual) {
        this.expected = expected;
        this.actual = actual;

        // Порог различий (например, 10%)
        double threshold = 0.1;

        // Размеры изображений
        int width = Math.min(expected.getWidth(), actual.getWidth());
        int height = Math.min(expected.getHeight(), actual.getHeight());

        // Общее количество пикселей
        int totalPixels = width * height;

        // Счётчик отличающихся пикселей
        int mismatchCount = 0;

        // Проходим по всем пикселям
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int expectedRGB = expected.getRGB(x, y);
                int actualRGB = actual.getRGB(x, y);

                // Вычисляем разницу между RGB-значениями
                int redDiff = Math.abs(((expectedRGB >> 16) & 0xFF) - ((actualRGB >> 16) & 0xFF));
                int greenDiff = Math.abs(((expectedRGB >> 8) & 0xFF) - ((actualRGB >> 8) & 0xFF));
                int blueDiff = Math.abs((expectedRGB & 0xFF) - (actualRGB & 0xFF));

                // Если хотя бы один канал отличается более чем на порог
                if (redDiff > threshold * 255 || greenDiff > threshold * 255 || blueDiff > threshold * 255) {
                    mismatchCount++;
                }
            }
        }

        // Вычисляем процент отличающихся пикселей
        double mismatchPercentage = (double) mismatchCount / totalPixels;

        // Определяем, есть ли значительные различия
        this.hasDif = mismatchPercentage > threshold;
    }

    @Override
    public boolean getAsBoolean() {
        if (hasDif) {
            System.out.println("Difference found: " + hasDif);
            ScreenShotTestExtension.setExpected(expected);
            ScreenShotTestExtension.setActual(actual);
        }
        return hasDif;
    }
}