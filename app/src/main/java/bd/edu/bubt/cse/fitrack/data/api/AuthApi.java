package bd.edu.bubt.cse.fitrack.data.api;

import bd.edu.bubt.cse.fitrack.data.dto.LoginRequest;
import bd.edu.bubt.cse.fitrack.data.dto.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthApi {
    @Headers("Content-Type: application/json")
    @POST("auth/signin")
    Call<LoginResponse> login(@Body LoginRequest request);
}
