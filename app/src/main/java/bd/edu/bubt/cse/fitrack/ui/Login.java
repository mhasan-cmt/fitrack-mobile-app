package bd.edu.bubt.cse.fitrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import bd.edu.bubt.cse.fitrack.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        binding.btnLogin.setOnClickListener(v -> loginUser());

        binding.tvRegister.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    binding.btnLogin.setEnabled(false);
                    binding.progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Invalid Credentials!", Toast.LENGTH_LONG).show();
                        Log.e("LoginError", "Login failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                    binding.btnLogin.setEnabled(true);
                });
    }
}
