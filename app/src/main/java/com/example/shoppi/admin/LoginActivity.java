package com.example.shoppi.admin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppi.R;
import com.example.shoppi.inicio;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView session25Text = findViewById(R.id.session25Text);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonRegister);  // Cambio aquí: usar buttonLogin en lugar de buttonRegister

        sessionManager = new SessionManager(getApplicationContext());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            navigateToHome();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if ("admin".equals(username) && "admin".equals(password)) {
                    sessionManager.setLogin(true);
                    sessionManager.setUserId(-1);
                    navigateToAdminHome();
                } else {
                    if (databaseHelper.checkLogin(username, password)) {
                        sessionManager.setLogin(true);
                        sessionManager.setUserId(databaseHelper.getUserId(username));
                        navigateToHome();
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        session25Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, inicio.class);
        startActivity(intent);
        finish();
    }

    private void navigateToAdminHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
