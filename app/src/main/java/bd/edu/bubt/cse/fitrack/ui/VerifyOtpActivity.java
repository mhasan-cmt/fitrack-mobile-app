package bd.edu.bubt.cse.fitrack.ui;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseStatus;
import bd.edu.bubt.cse.fitrack.databinding.ActivityVerifyOtpBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private ActivityVerifyOtpBinding binding;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = getIntent().getStringExtra("email");

        binding.btnVerifyOtp.setOnClickListener(v -> verifyOtp());
        binding.btnResendOtp.setOnClickListener(v -> resendOtp());
    }

    private void verifyOtp() {
        String otp = binding.etOtp.getText().toString().trim();

        if (otp.isEmpty()) {
            binding.etOtp.setError("OTP is required");
            return;
        }

        binding.progressBar.setVisibility(VISIBLE);
        binding.btnVerifyOtp.setEnabled(false);

        RetrofitClient.getAuthApi(this).verify(otp).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerifyOtp.setEnabled(true);

                if (response.body() != null && response.body().getStatus() == ApiResponseStatus.SUCCESS) {
                    Intent intent = new Intent(VerifyOtpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyOtpActivity.this,
                            response.body() != null ? response.body().getResponse() : "Verification failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerifyOtp.setEnabled(true);
                Toast.makeText(VerifyOtpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOtp() {
        binding.progressBar.setVisibility(VISIBLE);
        binding.btnResendOtp.setEnabled(false);

        if (binding.tvError.getVisibility() == VISIBLE) {
            binding.tvError.setVisibility(View.GONE);
        }

        RetrofitClient.getAuthApi(this).resend(email).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnResendOtp.setEnabled(true);
                binding.tvError.setVisibility(VISIBLE);
                binding.tvError.setText(response.body() != null ? response.body().getResponse() : "Failed to resend OTP");
            }


            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnResendOtp.setEnabled(true);
                binding.tvError.setVisibility(VISIBLE);
                binding.tvError.setText(t.getMessage());
            }
        });
    }
}
