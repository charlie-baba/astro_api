package com.sankore.astro.generic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Obi on 25/05/2019
 */
public class PasswordGenerator {

    private static final int passwordLength = 10;
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALCHARS = "!#$%&()*+,-./:;<=>?@[]_{|}";

    public static String generate() {
        return generate(passwordLength);
    }

    public static String generate(int length) {
        List<String> charCategories = new ArrayList<>();
        charCategories.add(LOWER);
        charCategories.add(UPPER);
        charCategories.add(DIGITS);
        charCategories.add(SPECIALCHARS);

        Random random = new Random(System.nanoTime());
        StringBuilder password = new StringBuilder();
        int categorySize = charCategories.size();

        for (int i = 0; i < length; i++) {
            String category = charCategories.get(random.nextInt(categorySize));
            int position = random.nextInt(category.length());
            password.append(category.charAt(position));
        }
        System.out.println("new password is: "+ password);
        return new String(password);
    }
}
