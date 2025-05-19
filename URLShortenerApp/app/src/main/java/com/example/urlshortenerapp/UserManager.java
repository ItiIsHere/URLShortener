package com.example.urlshortenerapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class UserManager {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_PREMIUM = "isPremium";
    private static final String KEY_URLS_COUNT = "urlsCount";
    private static final String KEY_LAST_RESET = "lastReset";

    private static UserManager instance;
    private SharedPreferences prefs;
    private boolean isPremium;
    private int urlsCreatedThisMonth;
    private long lastResetDate;

    private UserManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isPremium = prefs.getBoolean(KEY_IS_PREMIUM, false);
        urlsCreatedThisMonth = prefs.getInt(KEY_URLS_COUNT, 0);
        lastResetDate = prefs.getLong(KEY_LAST_RESET, System.currentTimeMillis());
        checkMonthlyReset();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }

    private void checkMonthlyReset() {
        Calendar now = Calendar.getInstance();
        Calendar lastReset = Calendar.getInstance();
        lastReset.setTimeInMillis(lastResetDate);

        if (now.get(Calendar.MONTH) != lastReset.get(Calendar.MONTH) ||
                now.get(Calendar.YEAR) != lastReset.get(Calendar.YEAR)) {
            resetMonthlyCount();
        }
    }

    public boolean canCreateMoreUrls() {
        return isPremium || urlsCreatedThisMonth < 5;
    }

    public void incrementUrlCount() {
        urlsCreatedThisMonth++;
        prefs.edit().putInt(KEY_URLS_COUNT, urlsCreatedThisMonth).apply();
    }

    public void upgradeToPremium() {
        isPremium = true;
        prefs.edit().putBoolean(KEY_IS_PREMIUM, true).apply();
    }

    private void resetMonthlyCount() {
        urlsCreatedThisMonth = 0;
        lastResetDate = System.currentTimeMillis();
        prefs.edit()
                .putInt(KEY_URLS_COUNT, 0)
                .putLong(KEY_LAST_RESET, lastResetDate)
                .apply();
    }

    // Getters para la UI
    public boolean isPremium() { return isPremium; }
    public int getUrlsCount() { return urlsCreatedThisMonth; }
    public int getUrlsLimit() { return 5; }
}