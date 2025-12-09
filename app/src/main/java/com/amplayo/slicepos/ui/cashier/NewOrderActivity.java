package com.amplayo.slicepos.ui.cashier;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.local.AppDatabase;
import com.amplayo.slicepos.data.models.Order;
import com.amplayo.slicepos.data.models.OrderItem;
import com.amplayo.slicepos.ui.cashier.adapters.OrderItemsAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class NewOrderActivity extends AppCompatActivity implements PizzaBuilderDialog.OnPizzaConfigured {

    private AppDatabase db;
    private Order currentOrder;
    private long currentOrderId;
    private List<OrderItem> orderItems = new ArrayList<>();
    private OrderItemsAdapter adapter;
    private TextView tvTotal;
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        db = AppDatabase.getInstance(this);
        createOrder();

        tvTotal = findViewById(R.id.tvTotal);
        RecyclerView rvOrderItems = findViewById(R.id.rvOrderItems);
        Button btnAddPizza = findViewById(R.id.btnAddPizza);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        adapter = new OrderItemsAdapter(orderItems);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(adapter);

        btnAddPizza.setOnClickListener(v -> {
            PizzaBuilderDialog dialog = new PizzaBuilderDialog();
            dialog.show(getSupportFragmentManager(), "PizzaBuilder");
        });

        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void createOrder() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentOrder = new Order(null, "open", 0.0, System.currentTimeMillis(), "cashier_1");
            currentOrderId = db.orderDao().insert(currentOrder);
            currentOrder.setId(currentOrderId);
        });
    }

    @Override
    public void onConfigured(OrderItem item) {
        item.setOrderId(currentOrderId);
        orderItems.add(item);
        adapter.notifyDataSetChanged();

        totalAmount += item.getLineTotal();
        tvTotal.setText(String.format("Total: P%.2f", totalAmount));

        // Update DB
        Executors.newSingleThreadExecutor().execute(() -> {
            if (currentOrder != null) {
                currentOrder.setTotal(totalAmount);
                db.orderDao().update(currentOrder);
            }
        });
    }

    private void checkout() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (currentOrder != null) {
                currentOrder.setStatus("paid");
                db.orderDao().update(currentOrder);

                runOnUiThread(() -> {
                    Intent intent = new Intent(NewOrderActivity.this, PaymentActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}
