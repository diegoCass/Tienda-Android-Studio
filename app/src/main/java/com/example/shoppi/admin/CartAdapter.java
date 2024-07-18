package com.example.shoppi.admin;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoppi.R;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemClickListener onCartItemClickListener;

    public CartAdapter(List<CartItem> cartItems, OnCartItemClickListener onCartItemClickListener) {
        this.cartItems = cartItems;
        this.onCartItemClickListener = onCartItemClickListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        holder.textViewProductName.setText(cartItem.getProduct().getName());
        holder.textViewProductPrice.setText(String.format(Locale.getDefault(), "$%.2f", cartItem.getProduct().getPrice()));
        holder.textViewQuantity.setText(String.valueOf(cartItem.getQuantity()));

        Uri imageUri = Uri.parse(cartItem.getProduct().getImageUrl());
        Glide.with(holder.imageViewProduct.getContext())
                .load(imageUri)
                .into(holder.imageViewProduct);

        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = cartItem.getQuantity() + 1;
                cartItem.setQuantity(quantity);
                onCartItemClickListener.onQuantityChanged(cartItem.getProduct(), quantity);
                notifyDataSetChanged();
            }
        });

        holder.buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = cartItem.getQuantity() - 1;
                if (quantity > 0) {
                    cartItem.setQuantity(quantity);
                    onCartItemClickListener.onQuantityChanged(cartItem.getProduct(), quantity);
                } else {
                    onCartItemClickListener.onItemRemoved(cartItem.getProduct());
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // ViewHolder class
    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProductName, textViewProductPrice, textViewQuantity;
        ImageView imageViewProduct;
        Button buttonAdd, buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }

    // Interface for item click events
    public interface OnCartItemClickListener {
        void onQuantityChanged(Product product, int quantity);
        void onItemRemoved(Product product);
    }
}
