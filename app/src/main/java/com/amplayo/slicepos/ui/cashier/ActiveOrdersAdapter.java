package com.amplayo.slicepos.ui.cashier;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.amplayo.slicepos.data.models.Order;
import com.amplayo.slicepos.databinding.ItemActiveOrderBinding;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActiveOrdersAdapter extends RecyclerView.Adapter<ActiveOrdersAdapter.OrderViewHolder> {

    private List<Order> orders = new ArrayList<>();
    private final OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onMarkReady(Order order);

        void onViewDetails(Order order);
    }

    public ActiveOrdersAdapter(OnOrderActionListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActiveOrderBinding binding = ItemActiveOrderBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final ItemActiveOrderBinding binding;

        public OrderViewHolder(ItemActiveOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.tvOrderNumber.setText(String.format("#%d", order.getId()));
            binding.tvOrderPrice.setText(String.format("₱%.2f", order.getTotal()));

            // Calculate time elapsed
            long elapsedMillis = System.currentTimeMillis() - order.getCreatedAt();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60;
            binding.tvTimeElapsed.setText(String.format("Time Elapsed: %dm %ds", minutes, seconds));

            // Set customer (placeholder for now)
            binding.tvCustomer.setText("Customer: Walk-in");

            // Set items (placeholder - would need to query OrderItems)
            binding.tvItems.setText("Items: View details for items");

            // Set status badge
            String status = order.getStatus();
            binding.chipStatus.setText(getStatusDisplayName(status));
            binding.chipStatus
                    .setChipBackgroundColor(android.content.res.ColorStateList.valueOf(getStatusColor(status)));

            // Button actions
            binding.btnMarkReady.setOnClickListener(v -> listener.onMarkReady(order));
            binding.btnViewDetails.setOnClickListener(v -> listener.onViewDetails(order));

            // Hide "Mark as Ready" if already ready or paid
            if ("READY".equals(status) || "PAID".equals(status)) {
                binding.btnMarkReady.setVisibility(View.GONE);
            } else {
                binding.btnMarkReady.setVisibility(View.VISIBLE);
            }
        }

        private String getStatusDisplayName(String status) {
            switch (status) {
                case "OPEN":
                case "PENDING_PAYMENT":
                    return "Pending";
                case "PAID":
                    return "Kitchen";
                case "READY":
                    return "Ready";
                default:
                    return status;
            }
        }

        private int getStatusColor(String status) {
            switch (status) {
                case "OPEN":
                case "PENDING_PAYMENT":
                    return Color.parseColor("#FFC107"); // Yellow
                case "PAID":
                    return Color.parseColor("#FF9800"); // Orange (Kitchen)
                case "READY":
                    return Color.parseColor("#4CAF50"); // Green
                default:
                    return Color.parseColor("#9E9E9E"); // Gray
            }
        }
    }
}
