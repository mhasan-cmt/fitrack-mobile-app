package bd.edu.bubt.cse.fitrack.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import bd.edu.bubt.cse.fitrack.data.dto.ProfileResponse;
import bd.edu.bubt.cse.fitrack.databinding.FragmentProfileBinding;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeViewModel();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("loggedInUsername", null);

        if (username != null) {
            profileViewModel.getUserProfileData(username);
        }

        binding.btnEditProfile.setOnClickListener(v ->
                Toast.makeText(getContext(), "Edit profile", Toast.LENGTH_SHORT).show()
        );

        binding.btnChangePassword.setOnClickListener(v ->
                Toast.makeText(getContext(), "Change password", Toast.LENGTH_SHORT).show()
        );

        return root;
    }

    private void observeViewModel() {
        profileViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        profileViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        profileViewModel.getProfileState().observe(getViewLifecycleOwner(), profileState -> {
            if (profileState instanceof ProfileViewModel.ProfileState.Success) {
                ProfileViewModel.ProfileState.Success success = (ProfileViewModel.ProfileState.Success) profileState;
                ProfileResponse profile = success.getData();

                binding.tvUsername.setText(profile.getUsername());
                binding.tvEmail.setText(profile.getEmail());
                binding.tvFirstName.setText(profile.getFirstName());
                binding.tvLastName.setText(profile.getLastName());
                binding.tvPhone.setText(profile.getPhone());
                binding.tvGender.setText(profile.getGender());
                binding.tvDateOfBirth.setText(profile.getDateOfBirth());
                binding.tvAddress.setText(profile.getAddress());

            } else if (profileState instanceof ProfileViewModel.ProfileState.Error) {
                ProfileViewModel.ProfileState.Error error = (ProfileViewModel.ProfileState.Error) profileState;
                Toast.makeText(getContext(), "Failed to load profile: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

