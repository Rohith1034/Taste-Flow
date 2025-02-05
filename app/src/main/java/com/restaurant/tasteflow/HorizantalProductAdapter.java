package com.restaurant.tasteflow;

import static com.restaurant.tasteflow.ProductAdapter.cardLister;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HorizantalProductAdapter extends RecyclerView.Adapter<HorizantalProductAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Product> productList;

    public HorizantalProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.horizantal_card,parent,false);
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

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView productImg,productAddToCartBtn;
        private ConstraintLayout layout;
        private TextView productName,productPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.horizontal_card);
            productImg = itemView.findViewById(R.id.productimg);
            productAddToCartBtn = itemView.findViewById(R.id.product_add_btn);
            productName = itemView.findViewById(R.id.productname);
            productPrice = itemView.findViewById(R.id.productprice);
        }
    }

}
