package bd.edu.bubt.cse.fitrack.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.ProfileResponse;
import bd.edu.bubt.cse.fitrack.data.repository.AuthRepository;

public class ProfileViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<ProfileState> profileState = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<ProfileState> getProfileState() {
        return profileState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void getUserProfileData(String username) {
        isLoading.setValue(true);

        authRepository.getUserProfileData(username, new AuthRepository.AuthCallback<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse result) {
                isLoading.postValue(false);
                profileState.postValue(new ProfileState.Success(result));
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                profileState.postValue(new ProfileState.Error(errorMsg));
            }
        });
    }



    public static abstract class ProfileState {

        public static class Loading extends ProfileState {
            // Empty class
        }

        public static class Success extends ProfileState {
            private final ProfileResponse data;

            public Success(ProfileResponse data) {
                this.data = data;
            }

            public ProfileResponse getData() {
                return data;
            }
        }

        public static class Error extends ProfileState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }
        }
    }
}
