package bd.edu.bubt.cse.fitrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
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

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnVerifyOtp.setEnabled(false);

        RetrofitClient.getAuthApi().verify(otp).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerifyOtp.setEnabled(true);

                Toast.makeText(VerifyOtpActivity.this,
                        response.body() != null ? response.body().getResponse() : "Verification failed",
                        Toast.LENGTH_SHORT).show();
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
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnResendOtp.setEnabled(false);

        RetrofitClient.getAuthApi().resend(email).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnResendOtp.setEnabled(true);

                Toast.makeText(VerifyOtpActivity.this,
                        response.body() != null ? response.body().getResponse() : "Failed to resend OTP",
                        Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnResendOtp.setEnabled(true);
                Toast.makeText(VerifyOtpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
