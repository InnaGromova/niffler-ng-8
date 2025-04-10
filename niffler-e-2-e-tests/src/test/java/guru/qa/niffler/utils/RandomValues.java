package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import java.util.Random;

public class RandomValues {
    public static final Faker faker = new Faker();

    public static final Random rand = new Random();
    public static String randomUserLogin() {
        return String.valueOf(faker.name());
    }
    public static String randomUserPassword() {
        return String.valueOf(faker.internet().password());
    }





}
