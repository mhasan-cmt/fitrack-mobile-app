package bd.edu.bubt.cse.fitrack.ui;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.LoginRequest;
import bd.edu.bubt.cse.fitrack.data.dto.LoginResponse;
import bd.edu.bubt.cse.fitrack.databinding.ActivityLoginBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.tvRegister.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
        binding.tvForgotPassword.setOnClickListener(v-> forgotPassword());
    }

    private void forgotPassword() {
        String email = binding.etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Enter a valid email address");
            return;
        }


    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Enter a valid email address");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            return;
        }

        binding.progressBar.setVisibility(VISIBLE);
        binding.btnLogin.setEnabled(false);

        LoginRequest loginRequest = new LoginRequest(email, password);

        RetrofitClient.getAuthApi().login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);

                if (binding.tvError.getVisibility() == VISIBLE) {
                    binding.tvError.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    saveToken(loginResponse.getToken());
                    saveUser(loginResponse.getUsername());

                    Toast.makeText(Login.this, "Welcome " + loginResponse.getUsername(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                } else {
                    binding.tvError.setVisibility(VISIBLE);
                    binding.tvError.setText(R.string.invalid_credentials);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
                binding.tvError.setVisibility(VISIBLE);
                binding.tvError.setText(t.getMessage());
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor  = sp.edit();
        editor.putString("jwt_token", token);
        editor.apply();
    }

    private void saveUser(String username) {
        SharedPreferences sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("loggedInUsername", username);
        editor.apply();
    }
}



