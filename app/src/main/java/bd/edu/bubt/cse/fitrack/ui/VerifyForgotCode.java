package bd.edu.bubt.cse.fitrack.ui;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseStatus;
import bd.edu.bubt.cse.fitrack.databinding.ActivityVerifyForgotCodeBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyForgotCode extends AppCompatActivity {

    private ActivityVerifyForgotCodeBinding binding;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyForgotCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");

        binding.btnVerify.setOnClickListener(v -> {
            String code = binding.etCode.getText().toString().trim();
            if (code.isEmpty()) {
                binding.etCode.setError("Code is required");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            RetrofitClient.getAuthApi().verifyResetPasswordCode(code).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    if (binding.tvError.getVisibility() == VISIBLE) {
                        binding.tvError.setVisibility(View.GONE);
                    }
                    if (response.isSuccessful() && response.body()!=null && response.body().getStatus() == ApiResponseStatus.SUCCESS) {
                        Intent intent = new Intent(VerifyForgotCode.this, ResetPassword.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
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

        binding.btnResend.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            if (binding.tvError.getVisibility() == VISIBLE) {
                binding.tvError.setVisibility(View.GONE);
            }
            RetrofitClient.getAuthApi().resendResetPasswordOTP(email).enqueue(new Callback<ApiResponseDto<String>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(VerifyForgotCode.this, "Verification email resent", Toast.LENGTH_SHORT).show();
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