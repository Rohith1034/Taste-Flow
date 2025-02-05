package com.restaurant.tasteflow;

import static android.R.layout.simple_list_item_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.type.Color;

import java.util.ArrayList;
import java.util.List;

public class ProductDescription extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ImageView productImg,backBtn,searchIcon,cartIcon;
    private RatingBar ratingBar;
    private BottomNavigationView bottomNavigationView;
    private TextView rating,productName,productPrice,strikedPrice,storageCondtions;
    private RecyclerView reviewRecycleView;
    private Button addReview,addToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_description);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent i = getIntent();
        String productId = i.getStringExtra("productId");
        databaseReference = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Products").child(productId);
        productImg = findViewById(R.id.product_img);
        ratingBar = findViewById(R.id.ratingBar);
        productName = findViewById(R.id.product_name);
        rating = findViewById(R.id.rating_text);
        productPrice = findViewById(R.id.productPrice);
        strikedPrice = findViewById(R.id.strikedPrice);
        storageCondtions = findViewById(R.id.storageConditions);
        reviewRecycleView = findViewById(R.id.review_recycleview);
        backBtn = findViewById(R.id.back_btn);
        LinearLayout container = findViewById(R.id.container);
        addReview = findViewById(R.id.add_review);
        addToCart = findViewById(R.id.add_to_cart);
        searchIcon = findViewById(R.id.search_icon);
        cartIcon = findViewById(R.id.cart_icon);
        bottomNavigationView = findViewById(R.id.bottomNav);

        addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDescription.this, AddReview.class);
                i.putExtra("productId",productId);
                startActivity(i);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    DatabaseReference db = FirebaseDatabase.getInstance("https://tasteflow-84914-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("Users")
                            .child(currentUser.getUid())  // Ensure user-specific cart
                            .child("cart");

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String productName = snapshot.child("ProductName").getValue(String.class);
                                String productImg = snapshot.child("productImg").getValue(String.class);
                                String productPrice = snapshot.child("ProductPrice").getValue(String.class);
                                int currentQuantity = 1;
                                final String[] found = new String[1];

                                db.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot cartSnapshot) {
                                        for (DataSnapshot itemSnapshot : cartSnapshot.getChildren()) {
                                            String key = itemSnapshot.getKey();
                                            if (itemSnapshot.getKey().length() < 2) {
                                                itemSnapshot.getRef().removeValue();
                                            }
                                            if (productId.equals(itemSnapshot.child("productId").getValue(String.class))) {
                                                found[0] = itemSnapshot.getKey();
                                                break;
                                            }
                                        }



                                        if (found[0] != null) {
                                            int updatedQuantity = cartSnapshot.child(found[0]).child("quantity").getValue(Integer.class) + 1;
                                            db.child(found[0]).child("quantity").setValue(updatedQuantity)
                                                    .addOnSuccessListener(aVoid -> Toast.makeText(ProductDescription.this, "Quantity Updated in Cart", Toast.LENGTH_SHORT).show());
                                        } else {
                                            String newKey = db.push().getKey(); // Generate unique key for each cart item
                                            Cart newCartItem = new Cart(productId, productName, productImg, currentQuantity, productPrice);
                                            db.child(newKey).setValue(newCartItem)
                                                    .addOnSuccessListener(aVoid -> Toast.makeText(ProductDescription.this, "Added To Cart", Toast.LENGTH_SHORT).show());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ProductDescription.this, "Failed to access cart data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ProductDescription.this, "Failed to access product data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProductDescription.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDescription.this,Search.class);
                startActivity(i);
            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDescription.this,CartPage.class);
                startActivity(i);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String u = snapshot.child("productImg").getValue(String.class);
                    if (u != null && !u.isEmpty()) {
                        Uri uri = Uri.parse(u);
                        Glide.with(ProductDescription.this)
                                .asBitmap()
                                .load(uri)
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache for better performance
                                        .format(DecodeFormat.PREFER_ARGB_8888)    // Higher quality images
                                        .override(Target.SIZE_ORIGINAL)           // Load full resolution
                                )
                                .into(productImg);


                    }
                    float rate = Float.valueOf(snapshot.child("ProductRating").getValue(String.class));
                    ratingBar.setRating(rate);
                    rating.setText("" + rate);
                    productName.setText(snapshot.child("ProductName").getValue(String.class));
                    productPrice.setText("₹ " + snapshot.child("ProductPrice").getValue(String.class));

                    Double price = Double.valueOf(snapshot.child("ProductPrice").getValue(String.class));
                    Integer striked = (int) (price + 10 % price);
                    SpannableString spannableString = new SpannableString("₹" + striked);
                    spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    strikedPrice.setText(spannableString);
                    storageCondtions.setText(snapshot.child("TermsAndConditionsOfStorage").getValue(String.class) + "And then place them in an airtight container.");
                    ArrayList<String> dataList = (ArrayList<String>) snapshot.child("Ingredients").getValue(new GenericTypeIndicator<List<String>>() {
                    });

                    for (int i = 0; i < dataList.size(); i += 2) {
                        LinearLayout rowLayout = new LinearLayout(ProductDescription.this);
                        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        Typeface typeface = ResourcesCompat.getFont(ProductDescription.this, R.font.poppinslight);
                        TextView textView1 = new TextView(ProductDescription.this);
                        textView1.setText("• " + dataList.get(i));
                        textView1.setTextSize(18);
                        textView1.setTextColor(getResources().getColor(R.color.white));
                        textView1.setPadding(10, 10, 10, 10);
                        textView1.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        textView1.setTypeface(typeface);


                        rowLayout.addView(textView1);

                        // Create Second TextView if exists
                        if (i + 1 < dataList.size()) {
                            TextView textView2 = new TextView(ProductDescription.this);
                            textView2.setText("• " + dataList.get(i + 1));
                            textView2.setTextSize(18);
                            textView2.setTextColor(getResources().getColor(R.color.white));

                            textView2.setPadding(10, 10, 10, 10);
                            textView2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            textView2.setTypeface(typeface);
                            rowLayout.addView(textView2);
                        }

                        container.addView(rowLayout);
                    }

                    ArrayList<Reviews> reviews = new ArrayList<>();
                    for (DataSnapshot reviewSnapshot : snapshot.child("Reviews").getChildren()) {
                        String userId = reviewSnapshot.child("UserId").getValue(String.class);
                        String userName = reviewSnapshot.child("UserName").getValue(String.class);
                        Double rating = reviewSnapshot.child("UserRating").getValue(Double.class);
                        String comment = reviewSnapshot.child("Comment").getValue(String.class);

                        if (userId == null && userName == null && rating == null && comment == null) {
                            userId = reviewSnapshot.child("userId").getValue(String.class);
                            userName = reviewSnapshot.child("userName").getValue(String.class);
                            rating = reviewSnapshot.child("userRating").getValue(Double.class);
                            comment = reviewSnapshot.child("comment").getValue(String.class);
                        }

                        if (userId != null && userName != null && rating != null && comment != null) {
                            Reviews review = new Reviews(userId, userName, rating, comment);
                            reviews.add(review);
                        }
                    }

                    ReviewAdapter reviewAdapter = new ReviewAdapter(ProductDescription.this, reviews);
                    reviewRecycleView.setLayoutManager(new LinearLayoutManager(ProductDescription.this, LinearLayoutManager.VERTICAL, false));
                    reviewRecycleView.setAdapter(reviewAdapter);
                    reviewAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    // Navigate to HomeActivity
                    startActivity(new Intent(ProductDescription.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    // Navigate to SearchActivity
                    startActivity(new Intent(ProductDescription.this, Search.class));
                    return true;
                } else if (item.getItemId() == R.id.cart) {
                    // Navigate to ProfileActivity
                    startActivity(new Intent(ProductDescription.this, CartPage.class));
                    return true;
                }else if (item.getItemId() == R.id.setting) {
                    startActivity(new Intent(ProductDescription.this, SettinsPage.class));
                }
                else {
                    return false;
                }
                return false;
            }
        });

    }
}