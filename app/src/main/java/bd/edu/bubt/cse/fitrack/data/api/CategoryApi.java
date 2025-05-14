package bd.edu.bubt.cse.fitrack.data.api;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryApi {
    @Headers("Content-Type: application/json")
    @GET("category")
    Call<ApiResponseDto<List<Category>>> getAllCategories();

    @Headers("Content-Type: application/json")
    @POST("category")
    Call<ApiResponseDto<Category>> createCategory(@Body Category category);

    @Headers("Content-Type: application/json")
    @GET("category/{id}")
    Call<ApiResponseDto<Category>> getCategoryById(@Path("id") long id);

    @Headers("Content-Type: application/json")
    @PUT("category/{id}")
    Call<ApiResponseDto<Category>> updateCategory(
            @Path("id") long id,
            @Body Category category
    );

    @Headers("Content-Type: application/json")
    @DELETE("category/{id}")
    Call<ApiResponseDto<Void>> deleteCategory(@Path("id") long id);
}