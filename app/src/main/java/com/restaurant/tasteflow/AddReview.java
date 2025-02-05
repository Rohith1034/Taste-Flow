package com.restaurant.tasteflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;

public class AddReview extends AppCompatActivity {
    private EditText comment, title;
    private RatingBar ratingBar;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_review);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String productId = getIntent().getStringExtra("productId");
        if (productId == null) {
            Toast.makeText(this, "Error: No product ID found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        comment = findViewById(R.id.review_comment);
        ratingBar = findViewById(R.id.ratingBar2);
        title = findViewById(R.id.review_title);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            if (!title.getText().toString().isEmpty() && !comment.getText().toString().isEmpty() && ratingBar.getRating() > 0) {
                String reviewComment = comment.getText().toString();
                Double rating = (double) ratingBar.getRating();
                String userId = auth.getCurrentUser().getUid();
                DatabaseReference documentReference = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(userId).child("name");
                DatabaseReference productsRef = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Products").child(productId).child("Reviews");

                documentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.getValue(String.class);
                        Reviews newReview = new Reviews(userId, userName, rating, reviewComment);
                        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int count = (int) snapshot.getChildrenCount();
                                String reviewKey = String.valueOf(count);

                                productsRef.child(reviewKey).setValue(newReview)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(AddReview.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(AddReview.this, ProductDescription.class);
                                            i.putExtra("productId", productId);
                                            startActivity(i);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(AddReview.this, "Save Failed! Retry", Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(AddReview.this, "Database Error!", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddReview.this, "Database Error!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(AddReview.this, "Enter All Fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
