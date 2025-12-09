package com.amplayo.slicepos.ui.cashier.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.models.OrderItem;

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.VH> {

    private final List<OrderItem> items;

    public OrderItemsAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_line, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        OrderItem item = items.get(pos);
        h.name.setText(item.getName());

        // Display config details if available, else placeholder
        if (item.getConfig() != null && !item.getConfig().isEmpty()) {
            h.details.setText(item.getConfig()); // In a real app, parse the JSON to readable text
        } else {
            h.details.setText("Standard Item");
        }

        h.total.setText(String.format("₱%.2f", item.getLineTotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, details, total;

        VH(View v) {
            super(v);
            name = v.findViewById(R.id.tvItemName);
            details = v.findViewById(R.id.tvItemDetails);
            total = v.findViewById(R.id.tvItemTotal);
        }
    }
}
