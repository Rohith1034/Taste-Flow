package com.restaurant.tasteflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;

public class SettinsPage extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView addressRecycleView;
    private ImageView profilePic,address_add,back_btn;
    private TextView name,email,phone;
    private LinearLayout address;
    private DatabaseReference db,users;
    private ConstraintLayout editProfile,orders,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settins_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNav);
        addressRecycleView = findViewById(R.id.address_recycle_view);
        profilePic = findViewById(R.id.profile_pic);
        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address_add = findViewById(R.id.address_add_btn);
        address = findViewById(R.id.address);
        editProfile = findViewById(R.id.edit_profile);
        orders = findViewById(R.id.orders);
        back_btn = findViewById(R.id.back_btn);
        logout = findViewById(R.id.logout);

        db = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(FirebaseAuth.getInstance().getUid().toString());

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Glide.with(SettinsPage.this).load(snapshot.child("profilePic").getValue(String.class)).into(profilePic);
                    name.setText("+91"+snapshot.child("phone").getValue(String.class));
                    email.setText(snapshot.child("email").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        users = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(FirebaseAuth.getInstance().getUid().toString()).child("address");

        ArrayList<Address> addressArrayList = new ArrayList<>();
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (item.getKey().equals("0")) {
                            item.getRef().removeValue();
                        }
                        else {
                            Address newAddress = new Address(
                                    item.child("addressId").getValue(String.class),
                                    item.child("street").getValue(String.class),
                                    item.child("city").getValue(String.class),
                                    item.child("zipCode").getValue(String.class),
                                    item.child("state").getValue(String.class),
                                    item.child("country").getValue(String.class),
                                    item.child("addressType").getValue(String.class));
                            addressArrayList.add(newAddress);
                        }

                    }

                    if (!addressArrayList.isEmpty()) {
                        address_add.setVisibility(View.VISIBLE);
                    }

                    addressRecycleView.setLayoutManager(new LinearLayoutManager(SettinsPage.this));
                    AddressCardAdapter adapter = new AddressCardAdapter(SettinsPage.this,addressArrayList);
                    addressRecycleView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressArrayList.isEmpty()) {
                    Intent i = new Intent(SettinsPage.this,AddAddressPage.class);
                    startActivity(i);
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    // Navigate to HomeActivity
                    startActivity(new Intent(SettinsPage.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    // Navigate to SearchActivity
                    startActivity(new Intent(SettinsPage.this, Search.class));
                    return true;
                } else if (item.getItemId() == R.id.cart) {
                    // Navigate to ProfileActivity
                    startActivity(new Intent(SettinsPage.this, CartPage.class));
                    return true;
                }else if (item.getItemId() == R.id.setting) {
                    startActivity(new Intent(SettinsPage.this, SettinsPage.class));
                }
                else {
                    return false;
                }
                return false;
            }
        });

        address_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettinsPage.this,AddAddressPage.class);
                startActivity(i);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettinsPage.this, EditProfile.class);
                startActivity(i);
            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(SettinsPage.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}