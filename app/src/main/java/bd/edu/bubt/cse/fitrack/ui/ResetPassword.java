package bd.edu.bubt.cse.fitrack.ui;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseStatus;
import bd.edu.bubt.cse.fitrack.data.dto.ResetPasswordRequestDto;
import bd.edu.bubt.cse.fitrack.databinding.ActivityResetPasswordBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private String email, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");

        binding.btnResetPassword.setOnClickListener(v -> {
            String password = binding.etPassword.getText().toString().trim();
            String confirm = binding.etConfirmPassword.getText().toString().trim();

            if (password.isEmpty() || password.length() < 8) {
                binding.etPassword.setError("Password must be at least 8 characters");
                return;
            }
            if (!password.equals(confirm)) {
                binding.etConfirmPassword.setError("Passwords do not match");
                return;
            }

            ResetPasswordRequestDto dto = new ResetPasswordRequestDto(email, password);
            binding.progressBar.setVisibility(View.VISIBLE);
            RetrofitClient.getAuthApi(this).resetForgotPassword(dto).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                    binding.progressBar.setVisibility(View.GONE);

                    if (binding.tvError.getVisibility() == VISIBLE) {
                        binding.tvError.setVisibility(View.GONE);
                    }

                    if (response.isSuccessful() && response.body()!=null && response.body().getStatus() == ApiResponseStatus.SUCCESS) {
                        Toast.makeText(ResetPassword.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPassword.this, Login.class));
                        finish();
                    } else {
                        binding.tvError.setVisibility(VISIBLE);
                        binding.tvError.setText(R.string.forgot_password_fallback_error_message);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvError.setVisibility(VISIBLE);
                    binding.tvError.setText(R.string.forgot_password_fallback_error_message);
                }
            });
        });
    }
}