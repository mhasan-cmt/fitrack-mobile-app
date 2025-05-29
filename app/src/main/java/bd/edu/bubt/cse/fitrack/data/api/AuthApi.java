package bd.edu.bubt.cse.fitrack.data.api;

import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.LoginRequest;
import bd.edu.bubt.cse.fitrack.data.dto.LoginResponse;
import bd.edu.bubt.cse.fitrack.data.dto.ProfileResponse;
import bd.edu.bubt.cse.fitrack.data.dto.RegisterRequest;
import bd.edu.bubt.cse.fitrack.data.dto.ResetPasswordRequestDto;
import bd.edu.bubt.cse.fitrack.data.dto.UpdateProfileRequest;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.LoginViewModel;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ProfileViewModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AuthApi {
    @Headers("Content-Type: application/json")
    @POST("auth/signin")
    Call<LoginResponse> login(@Body LoginRequest request);

    @Headers("Content-Type: application/json")
    @POST("auth/signup")
    Call<ApiResponseDto<String>> registerUser(@Body RegisterRequest request);

    @Headers("Content-Type: application/json")
    @GET("auth/signup/verify")
    Call<ApiResponseDto<String>> verify(@Query("code") String code);


    @Headers("Content-Type: application/json")
    @GET("auth/signup/resend")
    Call<ApiResponseDto<String>> resend(@Query("email") String email);

    @Headers("Content-Type: application/json")
    @GET("auth/forgotPassword/verifyEmail")
    Call<ApiResponseDto<String>> sendResetToken(@Query("email") String email);

    @Headers("Content-Type: application/json")
    @GET("auth/forgotPassword/verifyCode")
    Call<ApiResponseDto<String>> verifyResetPasswordCode(@Query("code") String code);

    @Headers("Content-Type: application/json")
    @POST("auth/forgotPassword/resetPassword")
    Call<ApiResponseDto<String>> resetForgotPassword(@Body ResetPasswordRequestDto request);

    @Headers("Content-Type: application/json")
    @GET("auth/forgotPassword/resendEmail")
    Call<ApiResponseDto<String>> resendResetPasswordOTP(@Query("email") String email);

    @Headers("Content-Type: application/json")
    @GET("user/get")
    Call<ApiResponseDto<ProfileResponse>> getUserProfileData(@Query("username") String username);
    @Headers("Content-Type: application/json")
    @PUT("user/update")
    Call<ApiResponseDto<ProfileResponse>> updateUserProfile(@Body UpdateProfileRequest updateProfileRequest);
}
