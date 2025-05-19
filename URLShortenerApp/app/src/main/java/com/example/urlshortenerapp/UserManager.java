package com.example.urlshortenerapp;

public class UserManager {
    private static UserManager instance;
    private int urlsCreatedThisMonth = 0;
    private boolean isPremium = false;

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public boolean canCreateMoreUrls() {
        return isPremium || urlsCreatedThisMonth < 5;
    }

    public static void setInstance(UserManager instance) {
        UserManager.instance = instance;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public int getUrlsCreatedThisMonth() {
        return urlsCreatedThisMonth;
    }

    public void setUrlsCreatedThisMonth(int urlsCreatedThisMonth) {
        this.urlsCreatedThisMonth = urlsCreatedThisMonth;
    }

    public void incrementUrlCount() {
        urlsCreatedThisMonth++;
    }
}