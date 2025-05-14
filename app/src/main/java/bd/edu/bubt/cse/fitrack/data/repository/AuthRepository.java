package bd.edu.bubt.cse.fitrack.data.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import bd.edu.bubt.cse.fitrack.data.api.AuthApi;
import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.LoginRequest;
import bd.edu.bubt.cse.fitrack.data.dto.LoginResponse;
import bd.edu.bubt.cse.fitrack.data.dto.RegisterRequest;
import bd.edu.bubt.cse.fitrack.data.dto.ResetPasswordRequestDto;
import bd.edu.bubt.cse.fitrack.data.local.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApi authApi;
    private final Context context;
    private final TokenManager tokenManager;

    public AuthRepository(Context context) {
        this.context = context;
        this.authApi = RetrofitClient.getAuthApi(context);
        this.tokenManager = TokenManager.getInstance(context);
    }

    public void login(String email, String password, AuthCallback<LoginResponse> callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No internet connection. Please check your network settings.");
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        authApi.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    tokenManager.saveToken(loginResponse.getToken());
                    tokenManager.saveUsername(loginResponse.getUsername());
                    callback.onSuccess(loginResponse);
                } else {
                    String errorMsg = "Invalid credentials";
                    if (response.code() == 401) {
                        errorMsg = "Invalid email or password";
                    } else if (response.code() == 403) {
                        errorMsg = "Account is locked. Please contact support.";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void register(RegisterRequest registerRequest, AuthCallback<ApiResponseDto<String>> callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No internet connection. Please check your network settings.");
            return;
        }

        authApi.registerUser(registerRequest).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Registration failed";
                    if (response.code() == 400) {
                        errorMsg = "Invalid registration data. Please check your inputs.";
                    } else if (response.code() == 409) {
                        errorMsg = "Email already registered. Please use a different email.";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void verifyOtp(String code, AuthCallback<ApiResponseDto<String>> callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No internet connection. Please check your network settings.");
            return;
        }

        authApi.verify(code).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Verification failed";
                    if (response.code() == 400) {
                        errorMsg = "Invalid verification code";
                    } else if (response.code() == 404) {
                        errorMsg = "Verification code not found or expired";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void resendOtp(String email, AuthCallback<ApiResponseDto<String>> callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No internet connection. Please check your network settings.");
            return;
        }

        authApi.resend(email).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to resend OTP";
                    if (response.code() == 400) {
                        errorMsg = "Invalid email address";
                    } else if (response.code() == 404) {
                        errorMsg = "Email not found";
                    } else if (response.code() == 429) {
                        errorMsg = "Too many requests. Please try again later.";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void sendResetToken(String email, AuthCallback<ApiResponseDto<String>> callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No internet connection. Please check your network settings.");
            return;
        }

        authApi.sendResetToken(email).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to send reset token";
                    if (response.code() == 400) {
                        errorMsg = "Invalid email address";
                    } else if (response.code() == 404) {
                        errorMsg = "Account not found for this email";
                    } else if (response.code() == 429) {
                        errorMsg = "Too many requests. Please try again later.";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void verifyResetCode(String code, AuthCallback<ApiResponseDto<String>> callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No internet connection. Please check your network settings.");
            return;
        }

        authApi.verifyResetPasswordCode(code).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to verify reset code";
                    if (response.code() == 400) {
                        errorMsg = "Invalid reset code";
                    } else if (response.code() == 404) {
                        errorMsg = "Reset code not found or expired";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void resetPassword(ResetPasswordRequestDto request, AuthCallback<ApiResponseDto<String>> callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No internet connection. Please check your network settings.");
            return;
        }

        authApi.resetForgotPassword(request).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to reset password";
                    if (response.code() == 400) {
                        errorMsg = "Invalid password format or reset code";
                    } else if (response.code() == 404) {
                        errorMsg = "Reset code not found or expired";
                    } else if (response.code() == 422) {
                        errorMsg = "Password must be at least 8 characters with letters and numbers";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void resendResetOtp(String email, AuthCallback<ApiResponseDto<String>> callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No internet connection. Please check your network settings.");
            return;
        }

        authApi.resendResetPasswordOTP(email).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to resend reset OTP";
                    if (response.code() == 400) {
                        errorMsg = "Invalid email address";
                    } else if (response.code() == 404) {
                        errorMsg = "Account not found for this email";
                    } else if (response.code() == 429) {
                        errorMsg = "Too many requests. Please try again later.";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Checks if the device has an active network connection
     * @return true if connected, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * Logs out the user by clearing all authentication data
     */
    public void logout() {
        tokenManager.clearAll();
    }

    public interface AuthCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}
