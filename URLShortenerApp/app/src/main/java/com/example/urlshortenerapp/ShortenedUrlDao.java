package com.example.urlshortenerapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ShortenedUrlDao {

    @Insert
    void insert(ShortenedUrl url);

    @Delete
    void delete(ShortenedUrl url);

    @Query("SELECT * FROM shortened_urls ORDER BY timestamp DESC")
    List<ShortenedUrl> getAllUrls();

    @Query("SELECT * FROM shortened_urls WHERE shortCode = :code LIMIT 1")
    ShortenedUrl getByCode(String code);

    @Query("DELETE FROM shortened_urls")
    void deleteAll();
}

