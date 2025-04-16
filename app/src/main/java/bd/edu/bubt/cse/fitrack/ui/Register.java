package bd.edu.bubt.cse.fitrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;
import java.util.regex.Pattern;

import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseStatus;
import bd.edu.bubt.cse.fitrack.data.dto.RegisterRequest;
import bd.edu.bubt.cse.fitrack.databinding.ActivityRegisterBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        EditText etUsername = binding.etUsername;
        EditText etEmail = binding.etEmail;
        EditText etPassword = binding.etPassword;
        Button btnRegister = binding.btnRegister;
        ProgressBar progressBar = binding.progressBar;

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!validateFields(etEmail, etPassword, etUsername)) return;

            btnRegister.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            Set<String> roles = Set.of("user");

            RegisterRequest request = new RegisterRequest(email, password, username, roles);
            RetrofitClient.getAuthApi().registerUser(request).enqueue(new Callback<ApiResponseDto>() {
                @Override
                public void onResponse(Call<ApiResponseDto> call, Response<ApiResponseDto> response) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals(ApiResponseStatus.SUCCESS)) {
                        Toast.makeText(Register.this, "OTP sent to email", Toast.LENGTH_SHORT).show();
                        Intent otpIntent = new Intent(Register.this, VerifyOtpActivity.class);
                        otpIntent.putExtra("email", email);
                        startActivity(otpIntent);
                        finish();
                    } else {
                        displayToast(response.body() != null ? response.body().getResponse().toString() : "Registration failed");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    displayToast("Error: " + t.getMessage());
                }
            });
        });
    }


    private boolean validateFields(EditText etEmail, EditText etPassword, EditText etUsername) {
        boolean isValid = true;

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            isValid = false;
        } else if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            isValid = false;
        } else if (username.length() > 20) {
            etUsername.setError("Username must be at most 20 characters");
            isValid = false;
        } else if (!username.matches("[a-zA-Z0-9]+")) {
            etUsername.setError("Username can only contain letters and numbers");
            isValid = false;
        }

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
        } else if (password.length() < 8) {
            etPassword.setError("Password must be at least 8 characters");
            isValid = false;
        } else if (password.length() > 20) {
            etPassword.setError("Password must be at most 20 characters");
            isValid = false;
        }

        return isValid;
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
