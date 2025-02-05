package com.restaurant.tasteflow;

import static com.restaurant.tasteflow.ProductAdapter.cardLister;

import android.content.Context;
import android.content.Intent;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>{

    private Context context;
    public ArrayList<Product> productList;

    public SearchAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        if (currentProduct != null && !currentProduct.getProductName().isEmpty() && !currentProduct.getProductId().isEmpty()) {
            holder.card.setTag(currentProduct.getProductId().toString());
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,ProductDescription.class);
                    i.putExtra("productId",v.getTag().toString());
                    context.startActivity(i);
                }
            });
            Uri uri = Uri.parse(currentProduct.getProductImg());
            Glide.with(context).load(uri).into(holder.productImg);
            holder.productName.setText(currentProduct.getProductName().toString());
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImg;
        private TextView productName;
        private ConstraintLayout card;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.search_card);
            productName = itemView.findViewById(R.id.product_name);
            productImg = itemView.findViewById(R.id.product_img);
        }
    }

}
