package com.example.urlshortenerapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import java.util.List;
import androidx.room.Query;

public class UrlRepository {
    private ShortenedUrlDao shortenedUrlDao;
    private LiveData<List<ShortenedUrl>> allUrls;

    public UrlRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        shortenedUrlDao = database.shortenedUrlDao();
        allUrls = shortenedUrlDao.getAllUrls();
    }

    public void insertUrl(ShortenedUrl url) {
        new InsertUrlAsyncTask(shortenedUrlDao).execute(url);
    }

    public void deleteUrl(int urlId) {
        new DeleteUrlAsyncTask(shortenedUrlDao).execute(urlId);
    }

    public LiveData<List<ShortenedUrl>> getAllUrls() {
        return allUrls;
    }

    public int getUrlCount() {
        return shortenedUrlDao.getUrlCount();
    }

    private static class InsertUrlAsyncTask extends AsyncTask<ShortenedUrl, Void, Void> {
        private ShortenedUrlDao dao;

        private InsertUrlAsyncTask(ShortenedUrlDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(ShortenedUrl... urls) {
            dao.insert(urls[0]);
            return null;
        }
    }

    private static class DeleteUrlAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ShortenedUrlDao dao;

        private DeleteUrlAsyncTask(ShortenedUrlDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Integer... ids) {
            dao.deleteUrl(ids[0]);
            return null;
        }
    }

    @Query("SELECT * FROM shortened_urls ORDER BY timestamp DESC")
    List<ShortenedUrl> getAllUrlsSync() {
        return null;
    }
}