package com.amplayo.slicepos.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amplayo.slicepos.R;
import com.amplayo.slicepos.data.local.AppDatabase;
import com.amplayo.slicepos.data.models.User;
import com.amplayo.slicepos.data.remote.FirebaseAuthHelper;
import com.amplayo.slicepos.ui.admin.AdminDashboardActivity;
import com.amplayo.slicepos.ui.cashier.CashierDashboardActivity;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuthHelper authHelper;
    private AppDatabase db;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progress;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authHelper = new FirebaseAuthHelper();
        db = AppDatabase.getInstance(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progress = findViewById(R.id.progress);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> handleLogin());

        tvForgotPassword.setOnClickListener(
                v -> Toast.makeText(this, "Forgot Password functionality coming soon", Toast.LENGTH_SHORT).show());
    }

    private void handleLogin() {
        hideKeyboard();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        authHelper.signIn(email, password, new FirebaseAuthHelper.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                // Check role
                String role = "cashier";
                if (firebaseUser.getEmail() != null && firebaseUser.getEmail().startsWith("admin")) {
                    role = "admin";
                }

                User user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), role);
                final String finalRole = role;

                Executors.newSingleThreadExecutor().execute(() -> {
                    db.userDao().insert(user);

                    runOnUiThread(() -> {
                        progress.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        Intent intent;
                        if ("admin".equals(finalRole)) {
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, CashierDashboardActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    });
                });
            }

            @Override
            public void onError(Exception e) {
                progress.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
