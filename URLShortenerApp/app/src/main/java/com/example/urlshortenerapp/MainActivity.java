package com.example.urlshortenerapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.urlshortener.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText etOriginalUrl;
    private TextView tvShortenedUrl, tvUserStatus;
    private Button btnShorten, btnCopy, btnHistory, btnUpgrade;
    private UrlRepository urlRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        urlRepository = new UrlRepository(this);
        etOriginalUrl = findViewById(R.id.etOriginalUrl);
        tvShortenedUrl = findViewById(R.id.tvShortenedUrl);
        tvUserStatus = findViewById(R.id.tvUserStatus);
        btnShorten = findViewById(R.id.btnShorten);
        btnCopy = findViewById(R.id.btnCopy);
        btnHistory = findViewById(R.id.btnHistory);
        btnUpgrade = findViewById(R.id.btnUpgrade);

        btnShorten.setOnClickListener(v -> shortenUrl());
        btnCopy.setOnClickListener(v -> copyToClipboard());
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        btnUpgrade.setOnClickListener(v -> startActivity(new Intent(this, UpgradeActivity.class)));

        updateUserStatusUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserManager.getInstance(this).checkPremiumStatus(this, new UserManager.OnPremiumCheckedListener() {
            @Override
            public void onChecked(boolean isPremium) {
                updateUserStatusUI();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(MainActivity.this, "Error al verificar estado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserStatusUI() {
        UserManager userManager = UserManager.getInstance(this);
        if (userManager.isPremium()) {
            tvUserStatus.setText("Estado: Premium");
            tvUserStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnUpgrade.setVisibility(View.GONE);
        } else {
            tvUserStatus.setText(String.format("Estado: Free (%d/%d URLs)",
                    userManager.getUrlsCount(),
                    userManager.getUrlsLimit()));
            tvUserStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnUpgrade.setVisibility(View.VISIBLE);
        }
    }

    private void shortenUrl() {
        UserManager userManager = UserManager.getInstance(this);
        String originalUrl = etOriginalUrl.getText().toString().trim();

        if (originalUrl.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese una URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userManager.canCreateMoreUrls()) {
            showUpgradeDialog();
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

                        // Guardar en base de datos
                        ShortenedUrl urlEntity = new ShortenedUrl();
                        urlEntity.originalUrl = originalUrl;
                        urlEntity.shortUrl = shortUrl;
                        urlEntity.timestamp = System.currentTimeMillis();
                        urlRepository.insertUrl(urlEntity);

                        // Actualizar UI
                        runOnUiThread(() -> {
                            tvShortenedUrl.setText("URL acortada: " + shortUrl);
                            userManager.incrementUrlCount();
                            updateUserStatusUI();
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

    private void showUpgradeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Límite alcanzado")
                .setMessage("Has alcanzado el límite de 5 URLs este mes. ¿Deseas hacerte Premium para URLs ilimitadas?")
                .setPositiveButton("Hacerme Premium", (dialog, which) ->
                        startActivity(new Intent(this, UpgradeActivity.class)))
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
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