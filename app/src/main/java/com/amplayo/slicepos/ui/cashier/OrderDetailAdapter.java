package com.amplayo.slicepos.ui.cashier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.models.OrderItem;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private List<OrderItem> items;

    public OrderDetailAdapter(List<OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.tvName.setText(item.getName());

        // Display config/details if available
        if (item.getConfig() != null && !item.getConfig().isEmpty()) {
            holder.tvDetails.setText(item.getConfig());
            holder.tvDetails.setVisibility(View.VISIBLE);
        } else {
            holder.tvDetails.setVisibility(View.GONE);
        }

        holder.tvPrice.setText(String.format("₱%.2f", item.getLineTotal()));

        // Hide remove button for read-only view
        if (holder.btnRemove != null) {
            holder.btnRemove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvPrice;
        View btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvDetails = itemView.findViewById(R.id.tvItemDetails);
            tvPrice = itemView.findViewById(R.id.tvItemTotal);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
