package bd.edu.bubt.cse.fitrack.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.api.ReportApi;
import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportRepository {
    private final ReportApi reportApi;

    public ReportRepository(Context context) {
        this.reportApi = RetrofitClient.getReportApi(context);
    }

    public void getMonthlySummaryByCategory(ReportCallback<List<CategorySummary>> callback) {
        reportApi.getSummaryGroupByCategory().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<CategorySummary>>> call, @NonNull Response<ApiResponseDto<List<CategorySummary>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get monthly summary by category: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<CategorySummary>>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getTotalIncomeOrExpense(int transactionType, int month, int year, ReportCallback<Double> callback) {
        reportApi.getTotalIncomeOrExpense(transactionType, month, year).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<Double>> call, @NonNull Response<ApiResponseDto<Double>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get total income or expense: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<Double>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface ReportCallback<T> {
        void onSuccess(T result);

        void onError(String errorMessage);
    }
}
