package com.example.urlshortenerapp;

import java.util.Random;

public class UrlUtils {
    private static final String CHAR_POOL = "abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;

    public static String generateShortCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        Random random = new Random();

        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }

        return sb.toString();
    }
}
