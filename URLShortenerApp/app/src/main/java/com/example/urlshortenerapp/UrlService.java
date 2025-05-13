package com.example.urlshortenerapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UrlService {
    @POST("shorten")
    Call<Void> shortenUrl(@Body UrlRequest request);
}

class UrlRequest {
    public String originalUrl;
    public String shortCode;

    public UrlRequest(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }
}
