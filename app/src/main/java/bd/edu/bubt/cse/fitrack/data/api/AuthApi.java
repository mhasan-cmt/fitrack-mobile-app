package bd.edu.bubt.cse.fitrack.data.api;

import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.LoginRequest;
import bd.edu.bubt.cse.fitrack.data.dto.LoginResponse;
import bd.edu.bubt.cse.fitrack.data.dto.RegisterRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
    @GET("auth/signup/verify")
    Call<ApiResponseDto<String>> resend(@Query("email") String email);

    @Headers("Content-Type: application/json")
    @GET("auth/forgotPassword")
    Call<ApiResponseDto<String>> resetPassword(@Query("email") String email);

    
}
