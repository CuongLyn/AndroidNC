package com.example.mypets.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;

import com.example.mypets.MainActivity;
import com.example.mypets.R;
import com.example.mypets.data.model.User;
import com.example.mypets.databinding.ActivityLoginBinding;
import com.example.mypets.utils.Resource;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupObservers();
        setupListeners();

        TextView txtSignupRedirect = findViewById(R.id.txtSignupRedirect);
        txtSignupRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void setupObservers() {
        authViewModel.getLoginResult().observe(this, resource -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);

            if (resource.status == Resource.Status.SUCCESS) {
                navigateToMain(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (isValidInput(email, password)) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setEnabled(false);
                authViewModel.login(email, password);
            }
        });
    }

    private boolean isValidInput(String email, String password) {
        if (email.isEmpty()) {
            binding.edtEmail.setError("Email không được trống");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError("Email không hợp lệ");
            return false;
        }
        if (password.isEmpty()) {
            binding.edtPassword.setError("Mật khẩu không được trống");
            return false;
        }
        return true;
    }

    private void navigateToMain(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_ROLE", user.getRole());
        startActivity(intent);
        finish();
    }
}