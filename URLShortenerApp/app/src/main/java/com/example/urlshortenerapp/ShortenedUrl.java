package com.example.urlshortenerapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shortened_urls")
public class ShortenedUrl {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userId; // Nuevo campo
    public String originalUrl;
    public String shortUrl;
    public long timestamp;
    public ShortenedUrl() {}

    // Constructor (ajusta según tu implementación)
    public ShortenedUrl(String userId, String originalUrl, String shortUrl) {
        this.userId = userId;
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.timestamp = System.currentTimeMillis();
    }
}