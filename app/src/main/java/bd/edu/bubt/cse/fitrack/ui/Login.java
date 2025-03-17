package bd.edu.bubt.cse.fitrack.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import bd.edu.bubt.cse.fitrack.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001; // Use consistent request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Web client ID from Firebase
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if the user is already signed in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        // Regular email/password login
        binding.btnLogin.setOnClickListener(v -> loginUser());

        // Redirect to register page
        binding.tvRegister.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));

        // Google Sign-In Button click
        binding.btnGoogleLogin.setOnClickListener(view -> signInWithGoogle());
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

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
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
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    } else {
                        displayToast("Authentication Failed: " + task.getException().getMessage());
                    }
                });
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}

