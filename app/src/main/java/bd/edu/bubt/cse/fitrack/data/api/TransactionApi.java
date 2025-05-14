package bd.edu.bubt.cse.fitrack.data.api;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TransactionApi {
    @Headers("Content-Type: application/json")
    @GET("transaction")
    Call<ApiResponseDto<List<Transaction>>> getAllTransactions(
            @Query("page") int page,
            @Query("size") int size
    );

    @Headers("Content-Type: application/json")
    @POST("transaction")
    Call<ApiResponseDto<Transaction>> createTransaction(@Body Transaction transaction);

    @Headers("Content-Type: application/json")
    @GET("transaction/{id}")
    Call<ApiResponseDto<Transaction>> getTransactionById(@Path("id") long id);

    @Headers("Content-Type: application/json")
    @PUT("transaction/{id}")
    Call<ApiResponseDto<Transaction>> updateTransaction(
            @Path("id") long id,
            @Body Transaction transaction
    );

    @Headers("Content-Type: application/json")
    @DELETE("transaction/{id}")
    Call<ApiResponseDto<Void>> deleteTransaction(@Path("id") long id);
}