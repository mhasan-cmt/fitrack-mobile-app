package bd.edu.bubt.cse.fitrack.ui;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.databinding.ActivityLoginBinding;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.LoginViewModel;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.tvRegister.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
        binding.tvForgotPassword.setOnClickListener(v -> forgotPassword());
    }

    private void observeViewModel() {
        viewModel.getLoginState().observe(this, loginState -> {
            if (loginState instanceof LoginViewModel.LoginState.Success) {
                LoginViewModel.LoginState.Success success = (LoginViewModel.LoginState.Success) loginState;
                Toast.makeText(Login.this, "Welcome " + success.getData().getUsername(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            } else if (loginState instanceof LoginViewModel.LoginState.Error) {
                LoginViewModel.LoginState.Error error = (LoginViewModel.LoginState.Error) loginState;
                binding.tvError.setVisibility(VISIBLE);
                binding.tvError.setText(error.getMessage());
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);

            if (!isLoading && binding.tvError.getVisibility() == VISIBLE) {
                binding.tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                if (errorMsg.contains("Email")) {
                    binding.etEmail.setError(errorMsg);
                } else if (errorMsg.contains("Password")) {
                    binding.etPassword.setError(errorMsg);
                }
            }
        });
    }

    private void forgotPassword() {
        Intent intent = new Intent(Login.this, ForgotPassword.class);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Let the ViewModel handle validation and login
        viewModel.login(email, password);
    }
}
