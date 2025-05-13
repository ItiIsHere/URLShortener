package com.example.urlshortenerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.urlshortener.R;
import com.example.urlshortenerapp.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vinculación de la vista
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Configuración del cliente de inicio de sesión de Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configurar el botón de inicio de sesión
        binding.btnGoogleSignin.setOnClickListener(v -> signInWithGoogle());
    }

    // Método para iniciar sesión con Google
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    // Manejo de la respuesta del inicio de sesión
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                // El inicio de sesión fue exitoso, navega a MainActivity
                GoogleSignInAccount account = task.getResult();
                Toast.makeText(this, "Bienvenido, " + account.getDisplayName(), Toast.LENGTH_SHORT).show();

                // Navegar a MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();  // Finaliza LoginActivity
            } else {
                // Si el inicio de sesión falla, muestra un mensaje
                Toast.makeText(this, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
