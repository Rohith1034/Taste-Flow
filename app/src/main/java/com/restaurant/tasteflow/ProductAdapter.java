package com.restaurant.tasteflow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private static Context context;
    private ArrayList<Product> productList;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public static void cardLister(View v) {
        Intent i = new Intent(context,ProductDescription.class);
        i.putExtra("productId",v.getTag().toString());
        context.startActivity(i);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        if (currentProduct != null && !currentProduct.getProductName().isEmpty() && !currentProduct.getProductId().isEmpty()) {
            holder.layout.setTag(currentProduct.getProductId().toString());
            holder.layout.setOnClickListener(v->cardLister(v));
            Uri uri = Uri.parse(currentProduct.getProductImg());
            Glide.with(context).load(uri).into(holder.productImg);
            if (currentProduct.getProductName().toString().length() < 17) {
                holder.productName.setText(currentProduct.getProductName().toString());
            }
            else {
                String trimString = currentProduct.getProductName().toString().substring(0,14) + "...";
                holder.productName.setText(trimString);
            }
            holder.productPrice.setText(String.valueOf(currentProduct.getProductPrice()));
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImg,productAddBtn;
        private TextView productName,productPrice;
        private CardView layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.product_card);
            productImg = itemView.findViewById(R.id.productimg);
            productName = itemView.findViewById(R.id.productname);
            productPrice = itemView.findViewById(R.id.productprice);
            productAddBtn = itemView.findViewById(R.id.product_add_btn);
        }
    }



}
