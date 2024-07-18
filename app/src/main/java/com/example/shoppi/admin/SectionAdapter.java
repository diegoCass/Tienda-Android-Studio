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

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    private List<Seccion> seccionList;
    private OnSectionClickListener onSectionClickListener;

    public SectionAdapter(List<Seccion> seccionList, OnSectionClickListener onSectionClickListener) {
        this.seccionList = seccionList;
        this.onSectionClickListener = onSectionClickListener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Seccion seccion = seccionList.get(position);
        holder.textViewSectionName.setText(seccion.getNombre());

        if (seccion.getSeccionImg() != null && !seccion.getSeccionImg().isEmpty()) {
            holder.imageViewSection.setImageURI(Uri.parse(seccion.getSeccionImg()));
        } else {
            holder.imageViewSection.setImageResource(R.drawable.estre);
        }
    }

    @Override
    public int getItemCount() {
        return seccionList.size();
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewSectionName;
        ImageView imageViewSection;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSectionName = itemView.findViewById(R.id.textViewSectionName);
            imageViewSection = itemView.findViewById(R.id.imageViewSection);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSectionClickListener.onSectionClick(getAdapterPosition());
        }
    }

    public interface OnSectionClickListener {
        void onSectionClick(int position);
    }
}
