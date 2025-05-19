package com.example.urlshortenerapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.urlshortener.R;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText etOriginalUrl;
    private TextView tvShortenedUrl;
    private Button btnShorten, btnCopy;
    private UrlRepository urlRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlRepository = new UrlRepository(this);

        etOriginalUrl = findViewById(R.id.etOriginalUrl);
        tvShortenedUrl = findViewById(R.id.tvShortenedUrl);
        btnShorten = findViewById(R.id.btnShorten);
        btnCopy = findViewById(R.id.btnCopy);

        btnShorten.setOnClickListener(v -> shortenUrl());
        btnCopy.setOnClickListener(v -> copyToClipboard());

        Button btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

    }

    private void shortenUrl() {
        String originalUrl = etOriginalUrl.getText().toString().trim();

        if (originalUrl.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese una URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!UserManager.getInstance().canCreateMoreUrls()) {
            Toast.makeText(this, "Límite mensual alcanzado (5 URLs)", Toast.LENGTH_SHORT).show();
            return;
        }

        String shortCode = UrlUtils.generateShortCode();
        UrlShortenerApi api = RetrofitClient.getInstance().create(UrlShortenerApi.class);
        UrlShortenerApi.ShortenRequest request = new UrlShortenerApi.ShortenRequest(originalUrl, shortCode);

        api.shortenUrl(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);

                        String shortUrl = json.has("shortUrl") ?
                                json.getString("shortUrl") :
                                RetrofitClient.BASE_URL + "/" + request.shortCode;


                        ShortenedUrl urlEntity = new ShortenedUrl();
                        urlEntity.originalUrl = originalUrl;
                        urlEntity.shortUrl = shortUrl;
                        urlEntity.timestamp = System.currentTimeMillis();
                        urlRepository.insertUrl(urlEntity);

                        runOnUiThread(() -> {
                            tvShortenedUrl.setText("URL acortada: " + shortUrl);
                            Toast.makeText(MainActivity.this, "URL acortada creada", Toast.LENGTH_SHORT).show();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void copyToClipboard() {
        String textToCopy = tvShortenedUrl.getText().toString().replace("URL acortada: ", "");
        if (!textToCopy.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("URL acortada", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "URL copiada al portapapeles", Toast.LENGTH_SHORT).show();
        }
    }
}