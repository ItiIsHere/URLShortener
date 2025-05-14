package com.example.urlshortenerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.urlshortener.R;
import com.example.urlshortenerapp.databinding.ActivityMainBinding;

import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnCopy.setOnClickListener(v -> copyToClipboard());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnShorten.setOnClickListener(v -> shortenUrl());
    }

    // Método para generar una URL corta aleatoria
    private void shortenUrl() {
        String originalUrl = binding.etOriginalUrl.getText().toString();

        if (originalUrl.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese una URL", Toast.LENGTH_SHORT).show();
            return;
        }

        String shortCode = UrlUtils.generateShortCode();
        UrlShortenerApi api = RetrofitClient.getInstance().create(UrlShortenerApi.class);
        UrlShortenerApi.ShortenRequest request = new UrlShortenerApi.ShortenRequest(originalUrl, shortCode);

        api.shortenUrl(request).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Extraer el shortUrl desde el JSON de la respuesta
                        String responseBody = response.body().string();
                        Log.d("RESPUESTA_BACKEND", responseBody);
                        org.json.JSONObject json = new org.json.JSONObject(responseBody);
                        String shortUrl = json.getString("shortUrl");

                        runOnUiThread(() -> {
                            binding.tvShortenedUrl.setText("URL acortada: " + shortUrl);
                            Toast.makeText(MainActivity.this, "URL enviada al servidor", Toast.LENGTH_SHORT).show();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error al leer la respuesta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error al acortar URL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    // Método que genera un identificador aleatorio para la URL corta
    private String generateShortenedUrl() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortUrl = new StringBuilder("short.ly/");

        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            shortUrl.append(characters.charAt(index));
        }

        return shortUrl.toString();
    }

    private void copyToClipboard() {
        String textToCopy = binding.tvShortenedUrl.getText().toString().replace("URL acortada: ", "");
        if (!textToCopy.isEmpty()) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("URL acortada", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "URL copiada al portapapeles", Toast.LENGTH_SHORT).show();
        }
    }


}
