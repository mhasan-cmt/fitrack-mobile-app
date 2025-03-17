package bd.edu.bubt.cse.fitrack.ui;

import static android.util.Patterns.EMAIL_ADDRESS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.databinding.ActivityRegisterBinding;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001; // Use consistent request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Web client ID from Firebase
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google Sign-In Button Click
        binding.btSignIn.setOnClickListener(view -> signInWithGoogle());

        // Login Redirect
        binding.tvLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });

        setupRegisterButton();
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) { // Use correct RC_SIGN_IN
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        firebaseAuthWithGoogle(googleSignInAccount.getIdToken());
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                    displayToast("Google sign-in failed: " + e.getMessage());
                }
            } else {
                displayToast("Google sign-in failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        displayToast("Welcome " + user.getDisplayName());
                        startActivity(new Intent(Register.this, MainActivity.class));
                        finish();
                    } else {
                        displayToast("Authentication Failed: " + task.getException().getMessage());
                    }
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

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));
                                finish();
                            } else {
                                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(Register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                Log.e("Register", "Registration error: " + errorMessage);
                                btnRegister.setEnabled(true);
                            }
                            progressBar.setVisibility(View.GONE);
                        });
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
