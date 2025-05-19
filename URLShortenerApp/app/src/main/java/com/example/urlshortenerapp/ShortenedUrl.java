package com.example.urlshortenerapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shortened_urls")
public class ShortenedUrl {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String originalUrl;
    public String shortUrl;
    public long timestamp;
    public ShortenedUrl() {}
}