package com.restaurant.tasteflow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddressCardAdapter extends RecyclerView.Adapter<AddressCardAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Address> addressArrayList;

    public AddressCardAdapter(Context context, ArrayList<Address> addressArrayList) {
        this.context = context;
        this.addressArrayList = addressArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.address_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Address currentAddress = addressArrayList.get(position);
        if (currentAddress != null) {
            String address = currentAddress.getStreet() + ", " + currentAddress.getCity() + ", "  + currentAddress.getZipCode();
            holder.address.setText(address);
            holder.addressType.setText(currentAddress.getAddressType());
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,AddAddressPage.class);
                    i.putExtra("address", currentAddress);
                    context.startActivity(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return addressArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView address,addressType;
        private ImageView btn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            addressType = itemView.findViewById(R.id.address_type);
            btn = itemView.findViewById(R.id.edit_address_btn);
        }
    }

}
