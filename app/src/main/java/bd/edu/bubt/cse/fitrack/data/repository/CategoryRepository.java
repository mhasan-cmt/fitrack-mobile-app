package bd.edu.bubt.cse.fitrack.data.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.api.CategoryApi;
import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.CreateCategoryRequest;
import bd.edu.bubt.cse.fitrack.data.local.TokenManager;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private final CategoryApi categoryApi;
    private final TokenManager tokenManager;
    private final Context context;

    public CategoryRepository(Context context) {
        this.categoryApi = RetrofitClient.getCategoryApi(context);
        this.tokenManager = TokenManager.getInstance(context);
        this.context = context;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public void getAllCategories(CategoryRepository.CategoryCallback<List<Category>> categoryCallback) {
        if (!isNetworkAvailable()) {
            categoryCallback.onError("No internet connection. Please check your network settings.");
            return;
        }

        categoryApi.getAllCategories().enqueue(new Callback<ApiResponseDto<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<Category>>> call, Response<ApiResponseDto<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryCallback.onSuccess(response.body().getResponse());
                } else {
                    String errorMsg = "Failed to fetch categories";
                    if (response.code() == 404) {
                        errorMsg = "Categories not found";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    } else if (response.body() != null) {
                        errorMsg = "Something went wrong";
                    }
                    categoryCallback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<Category>>> call, Throwable t) {
                categoryCallback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void addCategory(CreateCategoryRequest category, CategoryCallback<ApiResponseDto<String>> categoryCallback) {
        if (!isNetworkAvailable()) {
            categoryCallback.onError("No internet connection. Please check your network settings.");
            return;
        }

        categoryApi.createCategory(category).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryCallback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to add category";
                    if (response.code() == 400) {
                        errorMsg = "Invalid category data";
                    } else if (response.code() >= 500) {
                        errorMsg = "Server error. Please try again later.";
                    } else if (response.body() != null) {
                        errorMsg = "Something went wrong";
                    }
                    categoryCallback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                categoryCallback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface CategoryCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}
