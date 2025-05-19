package com.example.urlshortenerapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.urlshortenerapp.ShortenedUrl;

import java.util.List;

@Dao
public interface ShortenedUrlDao {
    @Insert
    void insert(ShortenedUrl url);

    @Query("SELECT * FROM shortened_urls ORDER BY timestamp DESC")
    LiveData<List<ShortenedUrl>> getAllUrls();

    @Query("DELETE FROM shortened_urls WHERE id = :urlId")
    void deleteUrl(int urlId);

    @Query("SELECT COUNT(*) FROM shortened_urls")
    int getUrlCount();
}
