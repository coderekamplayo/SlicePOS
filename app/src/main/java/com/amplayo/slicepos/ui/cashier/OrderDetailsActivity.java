package com.amplayo.slicepos.ui.cashier;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.local.AppDatabase;
import com.amplayo.slicepos.data.models.Order;
import com.amplayo.slicepos.data.models.OrderItem;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class OrderDetailsActivity extends AppCompatActivity {

    private AppDatabase db;
    private TextView tvOrderId, tvOrderStatus, tvOrderDate, tvTotalAmount;
    private RecyclerView rvOrderItems;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // 1. Get the ID passed from the previous screen
        orderId = getIntent().getLongExtra("ORDER_ID", -1);
        if (orderId == -1) {
            finish(); // Error: No ID passed
            return;
        }

        // 2. Initialize Views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(this);

        // 3. Load Data from Database
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // A. Fetch the Order Info
            Order order = db.orderDao().getById(orderId);

            // B. Fetch the Items for this Order
            List<OrderItem> items = db.orderItemDao().getByOrderId(orderId);

            // C. Update UI on Main Thread
            runOnUiThread(() -> {
                if (order != null) {
                    tvOrderId.setText("Order #" + order.getId());
                    tvOrderStatus.setText("Status: " + order.getStatus().toUpperCase());

                    // Format date
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
                    String formattedDate = sdf.format(new Date(order.getCreatedAt()));
                    tvOrderDate.setText(formattedDate);

                    tvTotalAmount.setText(String.format("₱%.2f", order.getTotal()));
                }

                // Set Adapter
                OrderDetailAdapter adapter = new OrderDetailAdapter(items);
                rvOrderItems.setAdapter(adapter);
            });
        });
    }
}
