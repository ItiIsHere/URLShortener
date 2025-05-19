package com.example.urlshortenerapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urlshortener.R;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UrlAdapter adapter;
    private UrlRepository urlRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Cierra esta actividad y regresa a la anterior

        urlRepository = new UrlRepository(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        urlRepository.getAllUrls().observe(this, urls -> {
            adapter = new UrlAdapter(urls, urlRepository);
            recyclerView.setAdapter(adapter);
        });
    }
}