package com.amplayo.slicepos.ui.cashier;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amplayo.slicepos.data.local.AppDatabase;
import com.amplayo.slicepos.data.models.Order;
import com.amplayo.slicepos.databinding.ActivityPaymentBinding;
import java.util.concurrent.Executors;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private AppDatabase db;
    private long orderId = -1;
    private double totalAmount = 0.0;
    private String selectedPaymentMethod = "credit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);

        // Get data from intent
        orderId = getIntent().getLongExtra("orderId", -1);
        totalAmount = getIntent().getDoubleExtra("total", 0.0);

        setupUI();
        setupPaymentMethods();
        setupButtons();
    }

    private void setupUI() {
        binding.tvAmount.setText(String.format("₱%.2f", totalAmount));
    }

    private void setupPaymentMethods() {
        // Set up radio button listeners
        binding.rbCredit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPaymentMethod = "credit";
                binding.llCardInputs.setVisibility(View.VISIBLE);
                binding.cardCredit.setStrokeWidth(4);
                binding.cardCash.setStrokeWidth(0);
                binding.cardMobile.setStrokeWidth(0);
                binding.rbCash.setChecked(false);
                binding.rbMobile.setChecked(false);
            }
        });

        binding.rbCash.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPaymentMethod = "cash";
                binding.llCardInputs.setVisibility(View.GONE);
                binding.cardCredit.setStrokeWidth(0);
                binding.cardCash.setStrokeWidth(4);
                binding.cardMobile.setStrokeWidth(0);
                binding.rbCredit.setChecked(false);
                binding.rbMobile.setChecked(false);
            }
        });

        binding.rbMobile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPaymentMethod = "mobile";
                binding.llCardInputs.setVisibility(View.GONE);
                binding.cardCredit.setStrokeWidth(0);
                binding.cardCash.setStrokeWidth(0);
                binding.cardMobile.setStrokeWidth(4);
                binding.rbCredit.setChecked(false);
                binding.rbCash.setChecked(false);
            }
        });

        // Make cards clickable
        binding.cardCredit.setOnClickListener(v -> binding.rbCredit.setChecked(true));
        binding.cardCash.setOnClickListener(v -> binding.rbCash.setChecked(true));
        binding.cardMobile.setOnClickListener(v -> binding.rbMobile.setChecked(true));
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnProcessPayment.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        // Validate card inputs if credit card is selected
        if ("credit".equals(selectedPaymentMethod)) {
            String cardNumber = binding.etCardNumber.getText().toString().trim();
            String expiry = binding.etExpiry.getText().toString().trim();
            String cvv = binding.etCvv.getText().toString().trim();

            if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Please fill in all card details", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Show loading overlay
        binding.loadingOverlay.setVisibility(View.VISIBLE);
        binding.btnProcessPayment.setEnabled(false);

        // Simulate 2-second processing delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Process payment in background
            Executors.newSingleThreadExecutor().execute(() -> {
                // Update order status to PAID
                Order order = db.orderDao().getById(orderId);
                if (order != null) {
                    order.setStatus("PAID");
                    order.setTotal(totalAmount);
                    db.orderDao().update(order);

                    // TODO: Deduct inventory stock for items sold
                    // This would require fetching order items and updating inventory
                    // For now, we'll skip this step

                    runOnUiThread(() -> {
                        binding.loadingOverlay.setVisibility(View.GONE);

                        // Navigate to success screen
                        Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("total", totalAmount);
                        intent.putExtra("paymentMethod", selectedPaymentMethod);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        binding.loadingOverlay.setVisibility(View.GONE);
                        binding.btnProcessPayment.setEnabled(true);
                        Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }, 2000); // 2-second delay
    }
}
