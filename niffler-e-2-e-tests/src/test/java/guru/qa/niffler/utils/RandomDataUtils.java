package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {
    public static final Faker faker = new Faker();

    public static String randomUserName() {
        return String.valueOf(faker.funnyName());
    }
    public static String randomName() {
        return String.valueOf(faker.name().name());
    }
    public static String randomSurname() {
        return String.valueOf(faker.name().lastName());
    }
    public static String randomCategoryName() {
        return String.valueOf(faker.music().instrument());
    }
    public static String randomSentence(int wordCount) {
        return faker.lorem().sentence(wordCount);
    }
    public static String randomUserPassword() {
        return String.valueOf(faker.internet().password());
    }
}
