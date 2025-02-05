package com.restaurant.tasteflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class CategoryPage extends AppCompatActivity {
    private LinearLayout veg,nonveg,fish,mutton,burger,italian,cakes,milshakes,beverages,sweets;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        veg = findViewById(R.id.veg);
        nonveg = findViewById(R.id.non_veg);
        fish = findViewById(R.id.fish);
        mutton = findViewById(R.id.mutton);
        burger = findViewById(R.id.burger);
        italian = findViewById(R.id.italian);
        cakes = findViewById(R.id.cakes);
        milshakes = findViewById(R.id.milkshakes);
        beverages = findViewById(R.id.beverages);
        sweets = findViewById(R.id.sweets);
        bottomNavigationView = findViewById(R.id.bottomNav);

        ArrayList<String> list = new ArrayList<>();

        veg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("veg");
                list.add("Vegetarian");
                list.add("Sweets");
                list.add("Cake");
                list.add("Beverages");
                list.add("Milkshake");
                list.add("Veg");

                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        nonveg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Chicken");
                list.add("Mutton");
                list.add("Meat");
                list.add("Seafood");
                list.add("Fish");
                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Seafood");
                list.add("Fish");
                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        mutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Mutton");
                list.add("Meat");
                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        burger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Burger");
                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        italian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Italian");
                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        cakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Cake");
                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        milshakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Milkshake");

                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        beverages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Beverages");

                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        sweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                list.add("Sweets");
                Intent i = new Intent(CategoryPage.this, AllProducts.class);
                i.putStringArrayListExtra("list",list);
                startActivity(i);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    // Navigate to HomeActivity
                    startActivity(new Intent(CategoryPage.this, Home.class));
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    // Navigate to SearchActivity
                    startActivity(new Intent(CategoryPage.this, Search.class));
                    return true;
                } else if (item.getItemId() == R.id.cart) {
                    // Navigate to ProfileActivity
                    startActivity(new Intent(CategoryPage.this, CartPage.class));
                    return true;
                }else if (item.getItemId() == R.id.setting) {
                    startActivity(new Intent(CategoryPage.this, SettinsPage.class));
                }
                else {
                    return false;
                }
                return false;
            }
        });

    }
}