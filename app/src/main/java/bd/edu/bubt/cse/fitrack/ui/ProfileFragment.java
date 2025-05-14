package bd.edu.bubt.cse.fitrack.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import bd.edu.bubt.cse.fitrack.R;

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnEditProfile;
    private Button btnChangePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvUsername = root.findViewById(R.id.tv_username);
        tvEmail = root.findViewById(R.id.tv_email);
        btnEditProfile = root.findViewById(R.id.btn_edit_profile);
        btnChangePassword = root.findViewById(R.id.btn_change_password);

        // Set up click listeners
        btnEditProfile.setOnClickListener(v -> {
            // TODO: Navigate to edit profile screen
            Toast.makeText(getContext(), "Edit profile", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            // TODO: Navigate to change password screen
            Toast.makeText(getContext(), "Change password", Toast.LENGTH_SHORT).show();
        });

        // Load user data
        loadUserData();

        return root;
    }

    private void loadUserData() {
        // In a real app, this would come from a repository or ViewModel
        // For now, we'll use SharedPreferences to get the username
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("loggedInUsername", "User");
        String email = "user@example.com"; // This would come from the API in a real app

        tvUsername.setText(username);
        tvEmail.setText(email);
    }
}