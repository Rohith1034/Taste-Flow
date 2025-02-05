package com.restaurant.tasteflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.restaurant.tasteflow.R;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private CircleImageView circleImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private TextView stateCity, textView5, viewAll;
    private ImageSlider imageSlider;
    private BlackProductCardAdapter adapter;
    private RecyclerView recyclerView, horizantalRecycleView, moreproducts;
    private DatabaseReference productReference;
    private ArrayList<Product> products;
    private ProductAdapter productAdapter;
    private LinearLayout loading,search;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        String uid = "";
        if (currentUser == null) {
            Log.e("FirebaseAuth", "No user is logged in");
        } else {
            uid = currentUser.getUid().toString();
            Log.d("FirebaseAuth", "User ID: " + uid);
        }
        databaseReference = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(uid);
        productReference = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Products");
        bottomNavigationView = findViewById(R.id.bottomNav);
        circleImageView = findViewById(R.id.profile_image);
        imageSlider = findViewById(R.id.image_slider);
        stateCity = findViewById(R.id.state_city);
        recyclerView = findViewById(R.id.recyclerView);
        horizantalRecycleView = findViewById(R.id.horizontal_recycleview);
        moreproducts = findViewById(R.id.moreproducts);
        textView5 = findViewById(R.id.textView5);
        loading = findViewById(R.id.loading);
        scrollView = findViewById(R.id.scrollView);
        viewAll = findViewById(R.id.viewall);
        search = findViewById(R.id.linearLayout2);

        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.img8, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img9, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img10, ScaleTypes.FIT));
        products = new ArrayList<>();
        imageSlider.setImageList(imageList);
        textView5.setText("Combination " + getTimePeriod());

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, AllProducts.class);
                startActivity(i);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this,Search.class);
                i.putExtra("searchQuery", "");
                startActivity(i);
            }
        });

        EditText searchEditText = findViewById(R.id.search_src_text);
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this,Search.class);
                startActivity(i);
            }
        });


        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products = new ArrayList<>();

                for (int i = 0; i < 5; i++) {
                    Random random = new Random();
                    int randNum = random.nextInt(136);
                    String[] pid = new String[1];
                    if (randNum < 10) {
                        pid[0] = "P0" + "0" + randNum;
                    } else if (randNum < 100) {
                        pid[0] = "P0" + randNum;
                    } else {
                        pid[0] = "P" + randNum;
                    }

                    List<Reviews> reviews = new ArrayList<>();
                    for (DataSnapshot reviewSnapshot : snapshot.child(pid[0]).child("reviews").getChildren()) {
                        String userId = reviewSnapshot.child("UserId").getValue(String.class);
                        String userName = reviewSnapshot.child("UserName").getValue(String.class);
                        Double rating = reviewSnapshot.child("UserRating").getValue(Double.class);
                        String comment = reviewSnapshot.child("Comment").getValue(String.class);

                        if (userId != null && userName != null && rating != null && comment != null) {
                            Reviews review = new Reviews(userId, userName, rating, comment);
                            reviews.add(review);
                        }
                    }

                    Double rating = Double.valueOf(snapshot.child(pid[0]).child("ProductRating").getValue(String.class));
                    if (rating == null) {
                        rating = 0.0;
                    }

                    Double price = Double.valueOf(snapshot.child(pid[0]).child("ProductPrice").getValue(String.class));
                    if (price == null) {
                        price = 0.0;
                    }

                    Product p = new Product(
                            snapshot.child(pid[0]).child("productid").getValue(String.class),
                            snapshot.child(pid[0]).child("category").getValue(String.class),
                            snapshot.child(pid[0]).child("productImg").getValue(String.class),
                            snapshot.child(pid[0]).child("ProductName").getValue(String.class),
                            rating,
                            price,
                            snapshot.child(pid[0]).child("ProductContains").getValue(String.class),
                            snapshot.child(pid[0]).child("Ingredients").getValue(new GenericTypeIndicator<List<String>>() {
                            }),
                            snapshot.child(pid[0]).child("TermsAndConditionsOfStorage").getValue(String.class),
                            reviews,
                            true
                    );
                    products.add(p);
                }

                ArrayList<Product> horizantalProducts = new ArrayList<>();

                for (int i = 0; i < 5; i++) {
                    Random random = new Random();
                    int randNum = random.nextInt(136);
                    String[] pid = new String[1];
                    if (randNum < 10) {
                        pid[0] = "P0" + "0" + randNum;
                    } else if (randNum < 100) {
                        pid[0] = "P0" + randNum;
                    } else {
                        pid[0] = "P" + randNum;
                    }

                    List<Reviews> reviews = new ArrayList<>();
                    for (DataSnapshot reviewSnapshot : snapshot.child(pid[0]).child("reviews").getChildren()) {
                        String userId = reviewSnapshot.child("UserId").getValue(String.class);
                        String userName = reviewSnapshot.child("UserName").getValue(String.class);
                        Double rating = reviewSnapshot.child("UserRating").getValue(Double.class);
                        String comment = reviewSnapshot.child("Comment").getValue(String.class);

                        if (userId != null && userName != null && rating != null && comment != null) {
                            Reviews review = new Reviews(userId, userName, rating, comment);
                            reviews.add(review);
                        }
                    }

                    Double rating = Double.valueOf(snapshot.child(pid[0]).child("ProductRating").getValue(String.class));
                    if (rating == null) {
                        rating = 0.0;
                    }

                    Double price = Double.valueOf(snapshot.child(pid[0]).child("ProductPrice").getValue(String.class));
                    if (price == null) {
                        price = 0.0;
                    }

                    Product p = new Product(
                            snapshot.child(pid[0]).child("productid").getValue(String.class),
                            snapshot.child(pid[0]).child("category").getValue(String.class),
                            snapshot.child(pid[0]).child("productImg").getValue(String.class),
                            snapshot.child(pid[0]).child("ProductName").getValue(String.class),
                            rating,
                            price,
                            snapshot.child(pid[0]).child("ProductContains").getValue(String.class),
                            snapshot.child(pid[0]).child("Ingredients").getValue(new GenericTypeIndicator<List<String>>() {
                            }),
                            snapshot.child(pid[0]).child("TermsAndConditionsOfStorage").getValue(String.class),
                            reviews,
                            true
                    );
                    horizantalProducts.add(p);
                }

                ArrayList<Product> moreProducts = new ArrayList<>();

                for (int i = 0; i < 20; i++) {
                    Random random = new Random();
                    int randNum = random.nextInt(136);
                    String[] pid = new String[1];
                    if (randNum < 10) {
                        pid[0] = "P0" + "0" + randNum;
                    } else if (randNum < 100) {
                        pid[0] = "P0" + randNum;
                    } else {
                        pid[0] = "P" + randNum;
                    }

                    String productId = pid.length > 0 ? pid[0] : "";  // Ensure there's at least one element in the array
                    if (!productId.isEmpty()) {
                        // Fetching the product data safely with null checks
                        String productIdVal = snapshot.child(productId).child("productid").getValue(String.class);
                        String category = snapshot.child(productId).child("category").getValue(String.class);
                        String productImg = snapshot.child(productId).child("productImg").getValue(String.class);
                        String productName = snapshot.child(productId).child("ProductName").getValue(String.class);
                        Double productRating = snapshot.child(productId).child("ProductRating").getValue(String.class) != null ?
                                Double.valueOf(snapshot.child(productId).child("ProductRating").getValue(String.class)) : 0.0;
                        Double productPrice = snapshot.child(productId).child("ProductPrice").getValue(String.class) != null ?
                                Double.valueOf(snapshot.child(productId).child("ProductPrice").getValue(String.class)) : 0.0;
                        String productContains = snapshot.child(productId).child("ProductContains").getValue(String.class);
                        List<String> ingredients = snapshot.child(productId).child("Ingredients").getValue(new GenericTypeIndicator<List<String>>() {
                        });
                        String termsOfStorage = snapshot.child(productId).child("TermsAndConditionsOfStorage").getValue(String.class);

                        // Ensuring non-null value for reviews
                        List<Reviews> reviews = new ArrayList<>();
                        for (DataSnapshot reviewSnapshot : snapshot.child(productId).child("reviews").getChildren()) {
                            String userId = reviewSnapshot.child("UserId").getValue(String.class);
                            String userName = reviewSnapshot.child("UserName").getValue(String.class);
                            Double rating = reviewSnapshot.child("UserRating").getValue(Double.class);
                            String comment = reviewSnapshot.child("Comment").getValue(String.class);

                            if (userId != null && userName != null && rating != null && comment != null) {
                                reviews.add(new Reviews(userId, userName, rating, comment));
                            }
                        }

                        // Create the Product object
                        Product p = new Product(
                                productIdVal,
                                category,
                                productImg,
                                productName,
                                productRating,
                                productPrice,
                                productContains,
                                ingredients,
                                termsOfStorage,
                                reviews,
                                true
                        );
                        moreProducts.add(p);
                    }

                }

                if (products.size() != 0) {
                    scrollView.setVisibility(View.VISIBLE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.INVISIBLE);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(Home.this, LinearLayoutManager.HORIZONTAL, false));
                productAdapter = new ProductAdapter(Home.this, products);
                recyclerView.setAdapter(productAdapter);

                horizantalRecycleView.setLayoutManager(new LinearLayoutManager(Home.this, LinearLayoutManager.VERTICAL, false));
                HorizantalProductAdapter horizantalProductAdapter = new HorizantalProductAdapter(Home.this, horizantalProducts);
                horizantalRecycleView.setAdapter(horizantalProductAdapter);

                moreproducts.setLayoutManager(new GridLayoutManager(Home.this, 2));
                BlackProductCardAdapter more = new BlackProductCardAdapter(Home.this, moreProducts);
                moreproducts.setAdapter(more);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load profile picture if available
                    if (snapshot.child("profilePic").exists()) {
                        String profilePicUrl = snapshot.child("profilePic").getValue(String.class);
                        if (profilePicUrl != null && !profilePicUrl.trim().isEmpty()) {
                            Glide.with(Home.this).load(profilePicUrl).into(circleImageView);
                        } else {
                            Log.e("FirebaseData", "Profile picture URL is null or empty.");
                        }
                    }

                    // Safely fetch the address list
                    DataSnapshot addressSnapshot = snapshot.child("address");
                    ArrayList<Address> addressList = new ArrayList<>();

                    if (addressSnapshot.exists()) {
                        for (DataSnapshot itemSnapshot : addressSnapshot.getChildren()) {
                            HashMap<String, Object> item = (HashMap<String, Object>) itemSnapshot.getValue();

                            if (item != null) {
                                String addressId = item.get("addressId") != null ? item.get("addressId").toString() : "";
                                String street = item.get("street") != null ? item.get("street").toString() : "";
                                String city = item.get("city") != null ? item.get("city").toString() : "";
                                String zipCode = item.get("zipCode") != null ? item.get("zipCode").toString() : "";
                                String state = item.get("state") != null ? item.get("state").toString() : "";
                                String country = item.get("country") != null ? item.get("country").toString() : "";
                                String addressType = item.get("addressType") != null ? item.get("addressType").toString() : "";

                                Address address = new Address(addressId,street, city, zipCode, state, country, addressType);
                                addressList.add(address);
                            }
                        }
                    } else {
                        Log.e("FirebaseData", "Address list is empty or does not exist.");
                    }


                    if (addressList.isEmpty()) {
                        stateCity.setText("City, State");
                    } else {
                        String state_City = addressList.get(0).getCity() + ", " + addressList.get(0).getState();
                        stateCity.setText(state_City);
                    }

                } else {
                    Log.e("FirebaseData", "Snapshot does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Database error: " + error.getMessage());
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    // Navigate to HomeActivity
                    startActivity(new Intent(Home.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    // Navigate to SearchActivity
                    startActivity(new Intent(Home.this, Search.class));
                    return true;
                } else if (item.getItemId() == R.id.cart) {
                    // Navigate to ProfileActivity
                    startActivity(new Intent(Home.this, CartPage.class));
                    return true;
                }else if (item.getItemId() == R.id.setting) {
                    startActivity(new Intent(Home.this, SettinsPage.class));
                }
                else {
                    return false;
                }
                return false;
            }
        });


    }

    private void loadMoreProducts() {
        Toast.makeText(Home.this, "All products", Toast.LENGTH_SHORT).show();
    }


    public String getTimePeriod() {
        LocalTime now = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now = LocalTime.now();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (now.getHour() >= 6 && now.getHour() < 12) {
                return "Breakfast";
            } else if (now.getHour() >= 12 && now.getHour() < 5) {
                return "Lunch";
            } else if (now.getHour() >= 5 && now.getHour() < 6) {
                return "Snacks";
            } else {
                return "Dinner";
            }
        }
        return "Lunch";
    }


}