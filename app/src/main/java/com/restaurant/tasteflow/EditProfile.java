package com.restaurant.tasteflow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restaurant.tasteflow.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EditProfile extends AppCompatActivity {
    private ImageView profilePic;
    private Uri imageUri;
    private DatabaseReference dbRef,db;
    private Button btn;
    private EditText username,email,phone,addressType, street, city, zipcode, state, country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profilePic = findViewById(R.id.profile_pic);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        addressType = findViewById(R.id.address_type);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        zipcode = findViewById(R.id.zipcode);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        btn = findViewById(R.id.submit);

        dbRef = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getUid());

        db = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("address");


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username.setText(snapshot.child("name").getValue(String.class));
                    email.setText(snapshot.child("email").getValue(String.class));
                    email.setFocusable(false);
                    email.setFocusableInTouchMode(false);
                    email.setClickable(false);
                    phone.setText(snapshot.child("phone").getValue(String.class));
                    Glide.with(EditProfile.this).load(snapshot.child("profilePic").getValue(String.class)).into(profilePic);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String[] addressId = {""};
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        addressId[0] = item.getKey().toString();
                        addressType.setText(item.child("addressType").getValue(String.class));
                        street.setText(item.child("street").getValue(String.class));
                        city.setText(item.child("city").getValue(String.class));
                        zipcode.setText(item.child("zipCode").getValue(String.class));
                        state.setText(item.child("state").getValue(String.class));
                        country.setText(item.child("country").getValue(String.class));
                        break;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().isEmpty() && !phone.getText().toString().isEmpty() &&!addressType.getText().toString().isEmpty() && !street.getText().toString().isEmpty() && !city.getText().toString().isEmpty()
                        && !zipcode.getText().toString().isEmpty() && !state.getText().toString().isEmpty() && !country.getText().toString().isEmpty()) {

                    if (addressId[0] != null) {
                        Address newAddress = new Address(
                                addressId[0],
                                street.getText().toString(),
                                city.getText().toString(),
                                zipcode.getText().toString(),
                                state.getText().toString(),
                                country.getText().toString(),
                                addressType.getText().toString());


                        db.child(addressId[0]).setValue(newAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditProfile.this, "Address Saved!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(EditProfile.this, SettinsPage.class);
                                startActivity(i);
                            }
                        });
                    }

                    dbRef.child("name").setValue(username.getText().toString());
                    dbRef.child("phone").setValue(phone.getText().toString());

                } else {
                    Toast.makeText(EditProfile.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
