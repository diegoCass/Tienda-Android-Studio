package com.example.shoppi.admin;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppi.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private OnItemClickListener onItemClickListener;
    private List<Producto> productList;

    public ProductAdapter(List<Producto> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Producto producto = productList.get(position);
        holder.textViewProductName.setText(producto.getNombre());
        holder.textViewProductPrice.setText(String.valueOf(producto.getPrecio()));
        holder.imageViewProduct.setImageURI(Uri.parse(producto.getImagenUri()));
        holder.textViewProductQR.setText(producto.getQr());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewProductName, textViewProductPrice, textViewProductQR;
        ImageView imageViewProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductQR = itemView.findViewById(R.id.textViewProductQR);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
