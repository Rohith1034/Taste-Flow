package com.restaurant.tasteflow;

import android.content.Intent;
import android.os.Build;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference collectionReference;
    private Button signup;
    private EditText name, email, phone;
    private TextInputEditText password;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/");
        collectionReference = database.getReference("Users");
        signup = findViewById(R.id.signup);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.passwordEditText);
        login = findViewById(R.id.login);

        if (currentUser != null) {
            // handle if already logged in.
            Intent i = new Intent(this, Home.class);
            startActivity(i);
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty() && !email.getText().toString().isEmpty() &&
                        !phone.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {

                    if (phone.getText().toString().length() != 10) {
                        Toast.makeText(SignUp.this,"Phone must be 10 digit",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (password.getText().toString().length() < 6) {
                        Toast.makeText(SignUp.this,"Password must 6 characters",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    currentUser = firebaseAuth.getCurrentUser();

                                    if (currentUser != null) {
                                        String userID = currentUser.getUid();
                                        ArrayList<Address> addresses = new ArrayList<>();
                                        addresses.add(new Address("1","", "", "", "", "",""));

                                        LocalDateTime myDateObj = null;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            myDateObj = LocalDateTime.now();
                                        }
                                        DateTimeFormatter myFormatObj = null;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                                        }
                                        String formattedDate = null;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            formattedDate = myDateObj.format(myFormatObj);
                                        }

                                        ArrayList<Order> orders = new ArrayList<>();
                                        orders.add(new Order("01", "Sample order", formattedDate, "1", "sample", 3, 70, ""));

                                        ArrayList<Cart> cartItems = new ArrayList<>();
                                        cartItems.add(new Cart("1", "sample", "",1, "100"));

                                        User newUser = new User(userID,
                                                name.getText().toString(),
                                                email.getText().toString(),
                                                password.getText().toString(),
                                                phone.getText().toString(),
                                                addresses,
                                                orders,
                                                "https://framerusercontent.com/images/acYHYh6pwrjOxUyQErb3AeTQmI.png",
                                                cartItems
                                        );

                                        collectionReference.child(userID).setValue(newUser)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(SignUp.this, "User successfully created!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(SignUp.this, Login.class));
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(SignUp.this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                });
                                    } else {
                                        Toast.makeText(SignUp.this, "Failed to retrieve user ID!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(SignUp.this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                } else {
                    Toast.makeText(SignUp.this, "Fill all details!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, Login.class);
                startActivity(i);
            }
        });

    }
}