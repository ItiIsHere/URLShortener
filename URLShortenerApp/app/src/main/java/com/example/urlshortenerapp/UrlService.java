package com.example.urlshortenerapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

class UrlRequest {
    public String originalUrl;
    public String shortCode;

    public UrlRequest(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }
}
