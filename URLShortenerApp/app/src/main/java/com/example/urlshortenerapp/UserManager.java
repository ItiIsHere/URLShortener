package com.example.urlshortenerapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManager {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_IS_PREMIUM = "isPremium";
    private static final String KEY_URLS_COUNT = "urlsCount";
    private static final String KEY_LAST_RESET = "lastReset";
    private static final String KEY_USER_ID = "userId";

    private static UserManager instance;
    private SharedPreferences prefs;
    private boolean isPremium;
    private int urlsCreatedThisMonth;
    private long lastResetDate;
    private String userId;

    private UserManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userId = prefs.getString(KEY_USER_ID, null);
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

    public void setUserId(String userId) {
        this.userId = userId;
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    // Getters
    public boolean isPremium() { return isPremium; }
    public int getUrlsCount() { return urlsCreatedThisMonth; }
    public int getUrlsLimit() { return 5; }

    public String getUserId() {
        return userId;
    }

    public void checkPremiumStatus(Context context, OnPremiumCheckedListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                UrlShortenerApi api = RetrofitClient.getInstance().create(UrlShortenerApi.class);
                api.checkUserStatus(new TokenRequest(token)).enqueue(new Callback<UserStatusResponse>() {
                    @Override
                    public void onResponse(Call<UserStatusResponse> call, Response<UserStatusResponse> response) {
                        if (response.isSuccessful()) {
                            isPremium = response.body().is_premium;
                            urlsCreatedThisMonth = response.body().urls_count;
                            prefs.edit()
                                    .putBoolean(KEY_IS_PREMIUM, isPremium)
                                    .putInt(KEY_URLS_COUNT, urlsCreatedThisMonth)
                                    .apply();
                            listener.onChecked(isPremium);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserStatusResponse> call, Throwable t) {
                        listener.onError(t);
                    }
                });
            }
        });
    }

    public void upgradeToPremium(Context context, OnPremiumCheckedListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            listener.onError(new Exception("Usuario no autenticado"));
            return;
        }

        user.getIdToken(false).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                UrlShortenerApi api = RetrofitClient.getInstance().create(UrlShortenerApi.class);
                api.upgradeToPremium(new TokenRequest(token)).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            isPremium = true;
                            prefs.edit().putBoolean(KEY_IS_PREMIUM, true).apply();
                            listener.onChecked(true);
                        } else {
                            listener.onError(new Exception("Error del servidor"));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        listener.onError(t);
                    }
                });
            } else {
                listener.onError(task.getException());
            }
        });
    }

    public interface OnPremiumCheckedListener {
        void onChecked(boolean isPremium);
        void onError(Throwable t);
    }

}