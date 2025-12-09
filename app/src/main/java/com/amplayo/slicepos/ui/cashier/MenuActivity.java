package com.amplayo.slicepos.ui.cashier;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.local.AppDatabase;
import com.amplayo.slicepos.data.models.OrderItem;
import com.amplayo.slicepos.ui.cashier.adapters.OrderItemsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuActivity extends AppCompatActivity implements PizzaBuilderDialog.OnPizzaConfigured {

    private AppDatabase db;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    // 1. The List and Adapter
    private List<OrderItem> cartItems = new ArrayList<>();
    private OrderItemsAdapter adapter;

    private double currentTotal = 0.0;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        db = AppDatabase.getInstance(this);

        // 2. Setup Views
        Button btnAddPizza = findViewById(R.id.btnAddPizza);
        Button btnCheckout = findViewById(R.id.btnCheckout);
        tvTotal = findViewById(R.id.tvTotal);
        RecyclerView rv = findViewById(R.id.rvOrderItems);

        // 3. Setup RecyclerView (Crucial Step!)
        adapter = new OrderItemsAdapter(cartItems);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Open Pizza Builder
        btnAddPizza.setOnClickListener(v -> {
            PizzaBuilderDialog dialog = new PizzaBuilderDialog();
            dialog.setListener(this);
            dialog.show(getSupportFragmentManager(), "PizzaBuilder");
        });

        // Checkout Logic
        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            io.execute(() -> {
                // Create Order
                com.amplayo.slicepos.data.models.Order newOrder = new com.amplayo.slicepos.data.models.Order();
                newOrder.setStatus("open");
                newOrder.setTotal(currentTotal);
                newOrder.setCreatedAt(System.currentTimeMillis());

                // Save to DB
                long newId = db.orderDao().insert(newOrder);

                // Navigate
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, PaymentActivity.class);
                    intent.putExtra("orderId", newId);
                    intent.putExtra("total", currentTotal);
                    startActivity(intent);
                });
            });
        });
    }

    @Override
    public void onConfigured(OrderItem item) {
        // 1. Update Math
        currentTotal += item.getLineTotal();
        tvTotal.setText(String.format("Total: ₱%.2f", currentTotal));

        // 2. Update List (Visuals)
        cartItems.add(item);
        adapter.notifyItemInserted(cartItems.size() - 1);

        Toast.makeText(this, "Added: " + item.getName(), Toast.LENGTH_SHORT).show();
    }
}
