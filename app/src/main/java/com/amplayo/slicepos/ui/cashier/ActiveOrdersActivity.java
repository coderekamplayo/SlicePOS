package com.amplayo.slicepos.ui.cashier;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.amplayo.slicepos.data.local.AppDatabase;
import com.amplayo.slicepos.data.models.Order;
import com.amplayo.slicepos.databinding.ActivityActiveOrdersBinding;
import java.util.List;
import java.util.concurrent.Executors;

public class ActiveOrdersActivity extends AppCompatActivity implements ActiveOrdersAdapter.OnOrderActionListener {

    private ActivityActiveOrdersBinding binding;
    private AppDatabase db;
    private ActiveOrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActiveOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);

        setupRecyclerView();
        loadActiveOrders();
        setupButtons();
    }

    private void setupRecyclerView() {
        adapter = new ActiveOrdersAdapter(this);
        binding.rvActiveOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvActiveOrders.setAdapter(adapter);
    }

    private void loadActiveOrders() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Query orders where status is not "COMPLETED"
            List<Order> orders = db.orderDao().getAll();
            List<Order> activeOrders = new java.util.ArrayList<>();
            for (Order order : orders) {
                if (!"COMPLETED".equals(order.getStatus())) {
                    activeOrders.add(order);
                }
            }

            runOnUiThread(() -> {
                if (activeOrders.isEmpty()) {
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                    binding.rvActiveOrders.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyState.setVisibility(View.GONE);
                    binding.rvActiveOrders.setVisibility(View.VISIBLE);
                    adapter.setOrders(activeOrders);
                }
            });
        });
    }

    private void setupButtons() {
        binding.btnMenu.setOnClickListener(v -> finish());

        binding.btnFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Filter functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMarkReady(Order order) {
        Executors.newSingleThreadExecutor().execute(() -> {
            order.setStatus("READY");
            db.orderDao().update(order);

            runOnUiThread(() -> {
                Toast.makeText(this, "Order marked as ready", Toast.LENGTH_SHORT).show();
                loadActiveOrders(); // Refresh list
            });
        });
    }

    @Override
    public void onViewDetails(Order order) {
        // For now, show a toast. In a full implementation, open OrderDetailsDialog or
        // Activity
        Toast.makeText(this, "Order #" + order.getId() + " details", Toast.LENGTH_SHORT).show();
        // TODO: Implement OrderDetailsDialog or navigate to OrderDetailsActivity
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveOrders(); // Refresh when returning to this screen
    }
}
