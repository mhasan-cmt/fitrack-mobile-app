package bd.edu.bubt.cse.fitrack.data.api;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportApi {
    @Headers("Content-Type: application/json")
    @GET("report/getMonthlySummaryByCategory")
    Call<ApiResponseDto<List<CategorySummary>>> getSummaryGroupByCategory();

    @Headers("Content-Type: application/json")
    @GET("report/getTotalIncomeOrExpense")
    Call<ApiResponseDto<Double>> getTotalIncomeOrExpense(@Query("transactionTypeId") int transactionType,
                                                         @Query("month") int month,
                                                         @Query("year") int year);
}
