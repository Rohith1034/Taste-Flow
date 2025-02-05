package com.restaurant.tasteflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAddressPage extends AppCompatActivity {
    private EditText addressType, street, city, zipcode, state, country;
    private Button btn;
    private DatabaseReference db;
    private Address existingAddress; // Variable to hold the received address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_address_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addressType = findViewById(R.id.address_type);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        zipcode = findViewById(R.id.zipcode);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        btn = findViewById(R.id.submit);

        db = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("address");

        // Retrieve address from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("address")) {
            existingAddress = (Address) intent.getSerializableExtra("address");
            if (existingAddress != null) {
                // Pre-fill EditText fields with existing address data
                addressType.setText(existingAddress.getAddressType());
                street.setText(existingAddress.getStreet());
                city.setText(existingAddress.getCity());
                zipcode.setText(existingAddress.getZipCode());
                state.setText(existingAddress.getState());
                country.setText(existingAddress.getCountry());
            }
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existingAddress != null) {
                    if (!addressType.getText().toString().isEmpty() && !street.getText().toString().isEmpty() && !city.getText().toString().isEmpty()
                            && !zipcode.getText().toString().isEmpty() && !state.getText().toString().isEmpty() && !country.getText().toString().isEmpty()) {
                        Address updateAddress = new Address(
                                existingAddress.getAddressId(),
                                street.getText().toString(),
                                city.getText().toString(),
                                zipcode.getText().toString(),
                                state.getText().toString(),
                                country.getText().toString(),
                                addressType.getText().toString());
                        db.child(existingAddress.getAddressId()).setValue(updateAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddAddressPage.this, "Update Successfully!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(AddAddressPage.this, SettinsPage.class);
                                startActivity(i);
                            }
                        });
                    }
                } else {

                    if (!addressType.getText().toString().isEmpty() && !street.getText().toString().isEmpty() && !city.getText().toString().isEmpty()
                            && !zipcode.getText().toString().isEmpty() && !state.getText().toString().isEmpty() && !country.getText().toString().isEmpty()) {
                        String newKey = db.push().getKey();
                        Address newAddress = new Address(
                                newKey,
                                street.getText().toString(),
                                city.getText().toString(),
                                zipcode.getText().toString(),
                                state.getText().toString(),
                                country.getText().toString(),
                                addressType.getText().toString());


                        db.child(newKey).setValue(newAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddAddressPage.this, "Address Saved!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(AddAddressPage.this, SettinsPage.class);
                                startActivity(i);
                            }
                        });

                    } else {
                        Toast.makeText(AddAddressPage.this, "Enter all fields", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
