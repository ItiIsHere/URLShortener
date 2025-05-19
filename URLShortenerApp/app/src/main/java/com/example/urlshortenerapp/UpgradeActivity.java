package com.example.urlshortenerapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.urlshortener.R;

public class UpgradeActivity extends AppCompatActivity {
    private EditText etCardNumber, etExpiry, etCvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCvv = findViewById(R.id.etCvv);
        Button btnUpgrade = findViewById(R.id.btnUpgrade);

        btnUpgrade.setOnClickListener(v -> {
            String cardNumber = etCardNumber.getText().toString().replaceAll("\\s+", "");
            String expiry = etExpiry.getText().toString();
            String cvv = etCvv.getText().toString();

            if (cardNumber.length() >= 16 && expiry.length() >= 4 && cvv.length() >= 3) {
                UserManager.getInstance(this).upgradeToPremium(this, new UserManager.OnPremiumCheckedListener() {
                    @Override
                    public void onChecked(boolean isPremium) {
                        if (isPremium) {
                            Toast.makeText(UpgradeActivity.this, "¡Ahora eres Premium!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(UpgradeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Datos de tarjeta incompletos", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
