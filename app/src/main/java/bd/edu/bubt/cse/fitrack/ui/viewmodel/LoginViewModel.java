package bd.edu.bubt.cse.fitrack.ui.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import bd.edu.bubt.cse.fitrack.data.dto.LoginResponse;
import bd.edu.bubt.cse.fitrack.data.repository.AuthRepository;

public class LoginViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void login(String email, String password) {
        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        isLoading.setValue(true);
        authRepository.login(email, password, new AuthRepository.AuthCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                isLoading.postValue(false);
                loginState.postValue(new LoginState.Success(result));
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                loginState.postValue(new LoginState.Error(errorMsg));
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            errorMessage.setValue("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Enter a valid email address");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            errorMessage.setValue("Password is required");
            return false;
        }

        return true;
    }

    public void logout() {
        authRepository.logout();
    }

    // State classes for login
    public static abstract class LoginState {

        public static class Loading extends LoginState {
            // Empty class
        }

        public static class Success extends LoginState {
            private final LoginResponse data;

            public Success(LoginResponse data) {
                this.data = data;
            }

            public LoginResponse getData() {
                return data;
            }
        }

        public static class Error extends LoginState {
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