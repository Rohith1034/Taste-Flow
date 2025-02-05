package com.restaurant.tasteflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartPage extends AppCompatActivity {
    private DatabaseReference db,cart;
    private RecyclerView recyclerView;
    private TextView streetCIty,subTotal,subTotal1,totalPrice;
    private ScrollView scrollView;
    private ImageView backBtn;
    private LinearLayout layout;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.cart_recycle_view);
        streetCIty = findViewById(R.id.street_city);
        subTotal = findViewById(R.id.subtotal_price);
        subTotal1 = findViewById(R.id.subtotal);
        totalPrice = findViewById(R.id.total_price);
        scrollView = findViewById(R.id.scrollView2);
        bottomNavigationView = findViewById(R.id.bottomNav);
        backBtn = findViewById(R.id.back_btn);


        db = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(currentUser.getUid());
        cart = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(currentUser.getUid()).child("cart");

        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Cart> cartArrayList = new ArrayList<>();
                    int price = 0;
                    for (DataSnapshot item : snapshot.getChildren()) {
                        if (item.getKey().equals("0")) {
                            item.getRef().removeValue();
                        } else {
                            price = price + (item.child("quantity").getValue(Integer.class) * Integer.valueOf(item.child("price").getValue(String.class)));
                            Cart cartItem = new Cart(item.child("productId").getValue(String.class),item.child("productName").getValue(String.class),item.child("productImg").getValue(String.class),item.child("quantity").getValue(Integer.class),item.child("price").getValue(String.class));
                            cartArrayList.add(cartItem);

                        }
                    }

                    if (cartArrayList.size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(CartPage.this,LinearLayoutManager.VERTICAL,false));
                        CartCardAdapter cartCardAdapter = new CartCardAdapter(CartPage.this,cartArrayList);
                        recyclerView.setAdapter(cartCardAdapter);
                        cartCardAdapter.notifyDataSetChanged();;
                        subTotal.setText("₹"+price);
                        subTotal1.setText("₹" + price);
                        totalPrice.setText("₹" + (price + 40));
                    } else {
                        scrollView.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("address")) {
                    ArrayList<Address> addressList = new ArrayList<>();

                    for (DataSnapshot addressSnapshot : snapshot.child("address").getChildren()) {
                        Map<String, Object> item = (Map<String, Object>) addressSnapshot.getValue();

                        if (item != null) {
                            Address address = new Address(
                                    item.get("addressId").toString(),
                                    item.get("street").toString(),
                                    item.get("city").toString(),
                                    item.get("zipCode").toString(),
                                    item.get("state").toString(),
                                    item.get("country").toString(),
                                    item.get("addressType").toString()
                            );

                            addressList.add(address);
                        }
                    }

                    if (!addressList.isEmpty()) {
                        String state_City = addressList.get(0).getStreet() + ", " + addressList.get(0).getCity();
                        streetCIty.setText(state_City);
                    } else {
                        streetCIty.setText("Street, City");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    // Navigate to HomeActivity
                    startActivity(new Intent(CartPage.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    // Navigate to SearchActivity
                    startActivity(new Intent(CartPage.this, Search.class));
                    return true;
                } else if (item.getItemId() == R.id.cart) {
                    // Navigate to ProfileActivity
                    startActivity(new Intent(CartPage.this, CartPage.class));
                    return true;
                }else if (item.getItemId() == R.id.setting) {
                    startActivity(new Intent(CartPage.this, SettinsPage.class));
                }
                else {
                    return false;
                }
                return false;
            }
        });

    }
}