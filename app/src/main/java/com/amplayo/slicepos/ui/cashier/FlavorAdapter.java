package com.amplayo.slicepos.ui.cashier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.models.PizzaFlavor;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class FlavorAdapter extends RecyclerView.Adapter<FlavorAdapter.FlavorViewHolder> {

    private List<PizzaFlavor> flavorList;
    private int selectedPosition = 0; // Default to the first item selected
    private OnFlavorSelectedListener listener;

    // Interface to send clicks back to your Activity
    public interface OnFlavorSelectedListener {
        void onFlavorSelected(PizzaFlavor flavor, int position);
    }

    public FlavorAdapter(List<PizzaFlavor> flavors, OnFlavorSelectedListener listener) {
        this.flavorList = flavors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FlavorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flavor_chip, parent, false);
        return new FlavorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlavorViewHolder holder, int position) {
        PizzaFlavor flavor = flavorList.get(position);
        holder.tvFlavorName.setText(flavor.getName());

        // LOGIC: Check if this item is the selected one
        if (selectedPosition == position) {
            // SELECTED STYLE: Red Background, White Text
            holder.cardView
                    .setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.tvFlavorName
                    .setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            holder.cardView.setStrokeWidth(0);
        } else {
            // UNSELECTED STYLE: White Background, Black Text, Grey Stroke
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
            holder.tvFlavorName
                    .setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
            holder.cardView.setStrokeColor(0xFFDDDDDD); // Light Grey
            holder.cardView.setStrokeWidth(2);
        }

        // Handle Click
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Refresh only the two items that changed (old selection vs new selection) for
            // performance
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onFlavorSelected(flavor, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return flavorList.size();
    }

    public static class FlavorViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlavorName;
        MaterialCardView cardView;

        public FlavorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFlavorName = itemView.findViewById(R.id.tvFlavorName);
            cardView = itemView.findViewById(R.id.cardFlavor);
        }
    }
}
