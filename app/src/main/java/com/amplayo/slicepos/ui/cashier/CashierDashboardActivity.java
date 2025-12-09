package com.amplayo.slicepos.ui.cashier;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.amplayo.slicepos.data.remote.FirebaseAuthHelper;
import com.amplayo.slicepos.databinding.ActivityCashierDashboardBinding;
import com.amplayo.slicepos.ui.auth.LoginActivity;

public class CashierDashboardActivity extends AppCompatActivity {

    private ActivityCashierDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCashierDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Navigation
        binding.btnNewOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, com.amplayo.slicepos.ui.cashier.MenuActivity.class));
        });

        binding.btnActiveOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, com.amplayo.slicepos.ui.cashier.ActiveOrdersActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            new FirebaseAuthHelper().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
