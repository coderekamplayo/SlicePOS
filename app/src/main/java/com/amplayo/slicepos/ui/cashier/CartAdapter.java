package com.amplayo.slicepos.ui.cashier;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.amplayo.slicepos.data.models.OrderItem;
import com.amplayo.slicepos.databinding.ItemCartRowBinding;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<OrderItem> items = new ArrayList<>();
    private final OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onEditItem(OrderItem item, int position);

        void onDeleteItem(OrderItem item, int position);
    }

    public CartAdapter(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<OrderItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartRowBinding binding = ItemCartRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartRowBinding binding;

        public CartViewHolder(ItemCartRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderItem item, int position) {
            binding.tvItemName.setText(item.getName());

            // Extract subtitle from config if available
            String subtitle = "Item details";
            if (item.getConfig() != null && !item.getConfig().isEmpty()) {
                try {
                    org.json.JSONObject config = new org.json.JSONObject(item.getConfig());
                    if (config.has("toppings")) {
                        org.json.JSONArray toppings = config.getJSONArray("toppings");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < Math.min(toppings.length(), 3); i++) {
                            if (i > 0)
                                sb.append(", ");
                            sb.append(toppings.getString(i));
                        }
                        if (toppings.length() > 3)
                            sb.append("...");
                        subtitle = sb.toString();
                    }
                } catch (Exception e) {
                    // Use default subtitle
                }
            }
            binding.tvItemSubtitle.setText(subtitle);

            binding.tvItemPrice.setText(String.format("₱%.2f", item.getLineTotal()));

            binding.ivEdit.setOnClickListener(v -> listener.onEditItem(item, position));
            binding.ivDelete.setOnClickListener(v -> listener.onDeleteItem(item, position));
        }
    }
}
