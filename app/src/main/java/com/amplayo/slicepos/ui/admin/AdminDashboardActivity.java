package com.amplayo.slicepos.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.remote.FirebaseAuthHelper;
import com.amplayo.slicepos.ui.auth.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseAuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        authHelper = new FirebaseAuthHelper();

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> handleLogout());
    }

    private void handleLogout() {
        authHelper.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
