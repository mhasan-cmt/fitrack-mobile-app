package bd.edu.bubt.cse.fitrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import bd.edu.bubt.cse.fitrack.databinding.ActivityRegisterBinding;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private static final Pattern EMAIL_ADDRESS =
            Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRegisterButton();

        // Login Redirect
        binding.tvLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });
    }

    private void setupRegisterButton() {
        EditText etEmail = binding.etEmail;
        EditText etPassword = binding.etPassword;
        Button btnRegister = binding.btnRegister;
        ProgressBar progressBar = binding.progressBar;

        btnRegister.setOnClickListener(v -> {
            if (validateFields(etEmail, etPassword)) {
                btnRegister.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Simulate local registration logic
                // You can replace this later with Retrofit or Room logic
                new Handler().postDelayed(() -> {
                    displayToast("Registration successful for " + email);
                    startActivity(new Intent(Register.this, Login.class));
                    finish();
                }, 1500);

                // NOTE: Replace this with Room insert or API call via Retrofit
            }
        });
    }

    private boolean validateFields(EditText etEmail, EditText etPassword) {
        boolean isValid = true;
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            isValid = false;
        } else if (!EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            isValid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        return isValid;
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
