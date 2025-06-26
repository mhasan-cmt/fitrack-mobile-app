package bd.edu.bubt.cse.fitrack.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.api.ReportApi;
import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.CategoryChartSummary;
import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import bd.edu.bubt.cse.fitrack.data.dto.DailySummary;
import bd.edu.bubt.cse.fitrack.data.dto.MonthlySummary;
import bd.edu.bubt.cse.fitrack.data.dto.PredictionSummary;
import bd.edu.bubt.cse.fitrack.data.dto.TransactionsCountSummary;
import bd.edu.bubt.cse.fitrack.data.dto.YearlySummary;
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

    public void getMonthlyIncomeExpenseData(int year, ReportCallback<List<MonthlySummary>> callback) {
        reportApi.getMonthlySummary(year).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<MonthlySummary>>> call, @NonNull Response<ApiResponseDto<List<MonthlySummary>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get monthly summary by category: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<MonthlySummary>>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });

    }

    public void getCategoryBreakdownSummary(int month, int year, ReportCallback<List<CategoryChartSummary>> callback) {
        reportApi.getCategoryBreakdown(month, year).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<CategoryChartSummary>>> call, @NonNull Response<ApiResponseDto<List<CategoryChartSummary>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get category breakdowns: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<CategoryChartSummary>>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });

    }

    public void getDailySummary(int month, int year, ReportCallback<List<DailySummary>> callback) {
        reportApi.getDailySummary(month, year).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<DailySummary>>> call, @NonNull Response<ApiResponseDto<List<DailySummary>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get category breakdowns: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<DailySummary>>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });

    }

    public void getYearlySummary(int year, ReportCallback<List<YearlySummary>> callback) {
        reportApi.getYearlySummary(year).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<List<YearlySummary>>> call, @NonNull Response<ApiResponseDto<List<YearlySummary>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get category breakdowns: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<List<YearlySummary>>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getTransactionsCountSummary(int month, int year, ReportCallback<TransactionsCountSummary> callback) {
        reportApi.getTransactionsCountSummary(month, year).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<TransactionsCountSummary>> call, @NonNull Response<ApiResponseDto<TransactionsCountSummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get transactions counts summary : " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<TransactionsCountSummary>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getPredictionSummary(int type, ReportCallback<PredictionSummary> callback) {
        reportApi.getPredictionForNextMonth(type).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseDto<PredictionSummary>> call, @NonNull Response<ApiResponseDto<PredictionSummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get prediction summary : " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseDto<PredictionSummary>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface ReportCallback<T> {
        void onSuccess(T result);

        void onError(String errorMessage);
    }
}
