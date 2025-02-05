package com.restaurant.tasteflow;

import static com.restaurant.tasteflow.ProductAdapter.cardLister;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
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

public class BlackProductCardAdapter extends RecyclerView.Adapter<BlackProductCardAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Product> productList;

    public BlackProductCardAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.black_product_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        if (currentProduct != null) {
            String productName = currentProduct.getProductName();
            String productId = currentProduct.getProductId();
            String productImg = currentProduct.getProductImg();
            Double productPrice = currentProduct.getProductPrice(); // Assuming it's a Double

            if (productName != null && productId != null && !productName.isEmpty() && !productId.isEmpty()) {
                holder.layout.setTag(productId);
                holder.layout.setOnClickListener(v -> cardLister(v));

                if (productImg != null) {
                    Uri uri = Uri.parse(productImg);
                    Glide.with(context).load(uri).into(holder.productImg);
                }

                if (productName.length() < 17) {
                    holder.productName.setText(productName);
                } else {
                    String trimString = productName.substring(0, 14) + "...";
                    holder.productName.setText(trimString);
                }

                holder.productPrice.setText(productPrice != null ? String.valueOf(productPrice) : "N/A");
            }
        }

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImg,productAddBtn;
        private TextView productName,productPrice;
        private ConstraintLayout layout;
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
