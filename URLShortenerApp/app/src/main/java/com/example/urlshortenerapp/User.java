package com.example.urlshortenerapp;

public class User {
    private static User instance;
    private boolean isPremium = false;
    private int urlsCreatedThisMonth = 0;

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public boolean canCreateMoreUrls() {
        return isPremium || urlsCreatedThisMonth < 5;
    }

    // Getters y setters
}