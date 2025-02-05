package com.restaurant.tasteflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Button login;
    private EditText email;
    private TextView warning,forgotPassword;
    private TextInputEditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        email = findViewById(R.id.email);
        password = findViewById(R.id.passwordEditText);
        forgotPassword = findViewById(R.id.forgot_password);
        login = findViewById(R.id.login);

        warning = findViewById(R.id.warning);

        if (currentUser != null) {
            Intent i = new Intent(Login.this, Home.class);
            startActivity(i);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                    warning.setText("Fill all details!");
                    warning.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    warning.setVisibility(View.VISIBLE);
                    return;
                }

                if (passwordInput.length() < 6) {
                    warning.setText("Password must be at least 6 characters!");
                    warning.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    warning.setVisibility(View.VISIBLE);
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                currentUser = firebaseAuth.getCurrentUser();
                                Intent i = new Intent(Login.this, Home.class);
                                startActivity(i);
                            } else {
                                String errorMessage;
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    errorMessage = "Incorrect email or password!";
                                }
                                warning.setText(errorMessage);
                                warning.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                warning.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,ForgetPaswword.class);
                startActivity(i);
            }
        });



    }
}