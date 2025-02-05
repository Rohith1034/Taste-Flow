package com.restaurant.tasteflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllProducts extends AppCompatActivity {
    private DatabaseReference documentReference;
    private ArrayList<Product> products;
    private RecyclerView recyclerView;
    private BlackProductCardAdapter productCardAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.all_products);
        bottomNavigationView = findViewById(R.id.bottomNav);
        recyclerView.setLayoutManager(new GridLayoutManager(AllProducts.this, 2));

        ArrayList<String> category = getIntent().getStringArrayListExtra("list");
        documentReference = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Products");
        products = new ArrayList<>();
        productCardAdapter = new BlackProductCardAdapter(this,products);
        recyclerView.setAdapter(productCardAdapter);

        if (category != null && !category.isEmpty()) {
            fetchProductsByCategory(category);
        } else {
            fetchAllProducts();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home) {
                    // Navigate to HomeActivity
                    startActivity(new Intent(AllProducts.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.filter) {
                    return true;
                }
                else if (item.getItemId() == R.id.category){
                    startActivity(new Intent(AllProducts.this, CategoryPage.class));
                    return true;
                }
                return false;
            }
        });

    }

    private void fetchProductsByCategory(ArrayList<String> categoryList) {
        documentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String productCategory = productSnapshot.child("category").getValue(String.class);

                    if (productCategory != null && categoryList.contains(productCategory)) {  // ✅ Match any category
                        Product p = new Product(
                                productSnapshot.child("productid").getValue(String.class),
                                productCategory,
                                productSnapshot.child("productImg").getValue(String.class),
                                productSnapshot.child("ProductName").getValue(String.class),
                                Double.valueOf(productSnapshot.child("ProductRating").getValue(String.class)),
                                Double.valueOf(productSnapshot.child("ProductPrice").getValue(String.class)),
                                productSnapshot.child("ProductContains").getValue(String.class),
                                productSnapshot.child("Ingredients").getValue(new GenericTypeIndicator<List<String>>() {}),
                                productSnapshot.child("TermsAndConditionsOfStorage").getValue(String.class),
                                new ArrayList<>(),  // Assuming empty reviews list
                                true
                        );
                        products.add(p);
                    }
                }
                productCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch data", error.toException());
            }
        });
    }



    private void fetchAllProducts() {
        documentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    // Manually creating Product object
                    Product product = new Product(
                            productSnapshot.child("productid").getValue(String.class),
                            productSnapshot.child("category").getValue(String.class),
                            productSnapshot.child("productImg").getValue(String.class),
                            productSnapshot.child("ProductName").getValue(String.class),
                            Double.valueOf(productSnapshot.child("ProductRating").getValue(String.class)),
                            Double.valueOf(productSnapshot.child("ProductPrice").getValue(String.class)),
                            productSnapshot.child("ProductContains").getValue(String.class),
                            productSnapshot.child("Ingredients").getValue(new GenericTypeIndicator<List<String>>() {}),
                            productSnapshot.child("TermsAndConditionsOfStorage").getValue(String.class),
                            new ArrayList<>(),  // Assuming reviews as an empty list
                            true  // Default boolean value
                    );

                    products.add(product);
                    if (products.size() > 40)
                        break;
                }
                productCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch data", error.toException());
            }
        });
    }

}
