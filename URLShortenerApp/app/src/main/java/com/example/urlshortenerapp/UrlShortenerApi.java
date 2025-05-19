package com.example.urlshortenerapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UrlShortenerApi {

    @POST("/shorten")
    Call<ResponseBody> shortenUrl(@Body ShortenRequest request);

    @POST("/api/users")
    Call<UserStatusResponse> checkUserStatus(@Body TokenRequest request);

    @POST("/api/users/upgrade")
    Call<Void> upgradeToPremium(@Body TokenRequest request);
    class ShortenRequest {
        String originalUrl;
        String shortCode;

        public ShortenRequest(String originalUrl, String shortCode) {
            this.originalUrl = originalUrl;
            this.shortCode = shortCode;
        }
    }
}
