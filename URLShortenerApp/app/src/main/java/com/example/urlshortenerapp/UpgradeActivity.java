package com.example.urlshortenerapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                UserManager.getInstance(this).upgradeToPremium();
                Toast.makeText(this, "¡Bienvenido a Premium!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Datos incompletos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
