package com.amplayo.slicepos.ui.cashier;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.amplayo.slicepos.data.local.AppDatabase;
import com.amplayo.slicepos.data.models.Order;
import com.amplayo.slicepos.data.models.OrderItem;
import com.amplayo.slicepos.databinding.ActivityCartBinding;
import java.util.List;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemActionListener {

    private ActivityCartBinding binding;
    private AppDatabase db;
    private CartAdapter adapter;
    private long orderId = -1;
    private static final double TAX_RATE = 0.08; // 8% tax

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);

        // Get order ID from intent
        orderId = getIntent().getLongExtra("orderId", -1);

        setupRecyclerView();
        loadCartItems();
        setupButtons();
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this);
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(adapter);
    }

    private void loadCartItems() {
        if (orderId == -1) {
            Toast.makeText(this, "No active order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            List<OrderItem> items = db.orderItemDao().getByOrderId(orderId);
            runOnUiThread(() -> {
                adapter.setItems(items);
                calculateTotals(items);
            });
        });
    }

    private void calculateTotals(List<OrderItem> items) {
        double subtotal = 0.0;
        for (OrderItem item : items) {
            subtotal += item.getLineTotal();
        }

        double taxes = subtotal * TAX_RATE;
        double total = subtotal + taxes;

        binding.tvSubtotal.setText(String.format("₱%.2f", subtotal));
        binding.tvTaxes.setText(String.format("₱%.2f", taxes));
        binding.tvTotal.setText(String.format("₱%.2f", total));
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.tvAddItems.setOnClickListener(v -> finish()); // Go back to menu

        binding.btnCheckout.setOnClickListener(v -> {
            // Update order status and total, then navigate to payment
            Executors.newSingleThreadExecutor().execute(() -> {
                Order order = db.orderDao().getById(orderId);
                if (order != null) {
                    double subtotal = 0.0;
                    for (OrderItem item : adapter.getItems()) {
                        subtotal += item.getLineTotal();
                    }
                    double total = subtotal + (subtotal * TAX_RATE);

                    order.setTotal(total);
                    order.setStatus("PENDING_PAYMENT");
                    db.orderDao().update(order);

                    runOnUiThread(() -> {
                        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("total", total);
                        startActivity(intent);
                        finish();
                    });
                }
            });
        });
    }

    @Override
    public void onEditItem(OrderItem item, int position) {
        // For now, show a toast. In a full implementation, you'd open the pizza builder
        // or item editor
        Toast.makeText(this, "Edit functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteItem(OrderItem item, int position) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.orderItemDao().delete(item);
            runOnUiThread(() -> {
                loadCartItems(); // Reload to refresh totals
                Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
