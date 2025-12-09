package com.amplayo.slicepos.ui.cashier;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.amplayo.slicepos.data.models.MenuItem;
import com.amplayo.slicepos.databinding.ItemMenuProductBinding;
import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    public MenuAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMenuProductBinding binding = ItemMenuProductBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new MenuViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private final ItemMenuProductBinding binding;

        public MenuViewHolder(ItemMenuProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MenuItem item) {
            binding.tvProductName.setText(item.getName());
            binding.tvProductPrice.setText(String.format("₱%.2f", item.getPrice()));
            // In a real app, load image from URL or Resource ID. Using placeholder or name
            // based logic.

            binding.btnAdd.setOnClickListener(v -> listener.onItemClick(item));
            binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
