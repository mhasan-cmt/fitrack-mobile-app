package bd.edu.bubt.cse.fitrack.ui;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseStatus;
import bd.edu.bubt.cse.fitrack.databinding.ActivityForgotPasswordBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnSendEmail.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                binding.etEmail.setError("Email is required");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            RetrofitClient.getAuthApi().sendResetToken(email).enqueue(new Callback<ApiResponseDto<String>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                    Log.d("DEBUG", "onResponse: " + response.body());
                    if (binding.tvError.getVisibility() == VISIBLE) {
                        binding.tvError.setVisibility(View.GONE);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body()!=null && response.body().getStatus() == ApiResponseStatus.SUCCESS) {
                        Toast.makeText(ForgotPassword.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPassword.this, VerifyForgotCode.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        binding.tvError.setVisibility(VISIBLE);
                        binding.tvError.setText((response.body()!=null && response.body().getResponse()!= null)  ? response.body().getResponse() : getString(R.string.forgot_password_fallback_error_message));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSendEmail.setEnabled(true);
                    binding.tvError.setVisibility(VISIBLE);
                    binding.tvError.setText(t.getMessage());
                }
            });
        });
    }
}
