package com.amplayo.slicepos.ui.cashier;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amplayo.slicepos.databinding.ActivityPaymentSuccessBinding;

public class PaymentSuccessActivity extends AppCompatActivity {

    private ActivityPaymentSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long orderId = getIntent().getLongExtra("orderId", -1);
        double total = getIntent().getDoubleExtra("total", 0.0);

        // Display order details
        binding.tvOrderNumber.setText(String.format("#SP%d", orderId));
        binding.tvTotalPaid.setText(String.format("₱%.2f", total));

        // Return to Dashboard button
        binding.btnReturnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, CashierDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Print Receipt button
        binding.btnPrintReceipt.setOnClickListener(v -> {
            Toast.makeText(this, "Printing Receipt...", Toast.LENGTH_SHORT).show();
            // In a real implementation, this would trigger a print job
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to payment screen
        Intent intent = new Intent(this, CashierDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
