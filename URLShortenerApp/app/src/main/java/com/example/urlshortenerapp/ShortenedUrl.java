package com.example.urlshortenerapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shortened_urls")
public class ShortenedUrl {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String originalUrl;
    public String shortCode;
    public long timestamp;

    public ShortenedUrl(String originalUrl, String shortCode, long timestamp) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.timestamp = timestamp;
    }
}
