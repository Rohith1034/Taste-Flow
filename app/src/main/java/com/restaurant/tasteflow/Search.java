package com.restaurant.tasteflow;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    private EditText searchEditText;
    private ImageView searchIcon,backBtn;
    private ConstraintLayout layout;
    private DatabaseReference databaseReference;
    private ArrayList<Product> productList = new ArrayList<>();
    private SearchAdapter productAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        // Initialize UI elements
        searchEditText = findViewById(R.id.search_src_text);
        searchIcon = findViewById(R.id.search_icon);
        recyclerView = findViewById(R.id.search_list);
        backBtn = findViewById(R.id.back_btn);
        layout = findViewById(R.id.main);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Products");

        // Set up RecyclerView
        productAdapter = new SearchAdapter(Search.this, productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(productAdapter);

        // Get query from intent
        String query = getIntent().getStringExtra("searchQuery");
        if (query != null) {
            searchEditText.setText(query);
        }

        searchEditText.requestFocus();
        searchEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 200);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!productList.isEmpty()) {
                    layout.setBackgroundColor(Color.parseColor("#141417"));
                    BlackProductCardAdapter cardAdapter = new BlackProductCardAdapter(Search.this, new ArrayList<>(productList));
                    productList.clear(); // Clear the main list
                    recyclerView.setLayoutManager(new GridLayoutManager(Search.this, 2));
                    recyclerView.setAdapter(cardAdapter);
                    cardAdapter.notifyDataSetChanged();
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 2) {
                    performSearch(s.toString());
                } else {
                    // If search is empty, clear the list (instead of showing all products)
                    productList.clear();
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void performSearch(String query) {
        Log.d("Search", "Search Query: " + query);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> filteredProducts = new ArrayList<>();

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
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

                    if (product != null && product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                        filteredProducts.add(product);
                    }
                }

                // Update the adapter with the filtered list
                productList.clear();
                productList.addAll(filteredProducts);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Search", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
