package bd.edu.bubt.cse.fitrack.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.ProfileResponse;
import bd.edu.bubt.cse.fitrack.data.dto.UpdateProfileRequest;
import bd.edu.bubt.cse.fitrack.databinding.FragmentProfileBinding;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private ArrayAdapter<CharSequence> genderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupGenderSpinner();

        observeViewModel();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("loggedInUsername", null);

        if (username != null) {
            profileViewModel.getUserProfileData(username);
        }

        binding.btnEditProfile.setOnClickListener(v -> {
            String firstName = binding.etFirstName.getText().toString().trim();
            String lastName = binding.etLastName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String gender = binding.spinnerGender.getSelectedItem().toString();
            String dateOfBirth = binding.etDateOfBirth.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(
                    firstName,
                    lastName,
                    phone,
                    gender,
                    dateOfBirth,
                    address
            );

            profileViewModel.updateUserProfile(
                    updateProfileRequest
            );
        });

        binding.btnChangePassword.setOnClickListener(v ->
                Toast.makeText(getContext(), "Change password", Toast.LENGTH_SHORT).show()
        );

        return root;
    }

    private void setupGenderSpinner() {
        // Setup spinner with gender options - you can customize as needed
        genderAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGender.setAdapter(genderAdapter);
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
                binding.etFirstName.setText(profile.getFirstName());
                binding.etLastName.setText(profile.getLastName());
                binding.etPhone.setText(profile.getPhone());

                setSpinnerSelection(binding.spinnerGender, profile.getGender());

                binding.etDateOfBirth.setText(profile.getDateOfBirth());
                binding.etAddress.setText(profile.getAddress());

            } else if (profileState instanceof ProfileViewModel.ProfileState.Error) {
                ProfileViewModel.ProfileState.Error error = (ProfileViewModel.ProfileState.Error) profileState;
                Toast.makeText(getContext(), "Failed to load profile: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (value.equalsIgnoreCase(spinner.getItemAtPosition(i).toString())) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


