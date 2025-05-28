package bd.edu.bubt.cse.fitrack.data.api;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.CreateTransactionRequest;
import bd.edu.bubt.cse.fitrack.data.dto.TransactionResponseWrapper;
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
    @GET("transaction/getByUser")
    Call<ApiResponseDto<TransactionResponseWrapper>> getAllTransactions(
            @Query("pageNumber") int page,
            @Query("pageSize") int size,
            @Query("searchKey") String searchKey,
            @Query("sortField") String sortField,
            @Query("sortDirec") String sortDirec,
            @Query("transactionType") String transactionType
    );
    @Headers("Content-Type: application/json")
    @POST("transaction/new")
    Call<ApiResponseDto<String>> createTransaction(@Body CreateTransactionRequest transaction);

    @Headers("Content-Type: application/json")
    @GET("transaction/{id}")
    Call<ApiResponseDto<Transaction>> getTransactionById(@Path("id") long id);

    @Headers("Content-Type: application/json")
    @PUT("transaction/update/{id}")
    Call<ApiResponseDto<String>> updateTransaction(
            @Path("transactionId") long id,
            @Body CreateTransactionRequest transaction
    );

    @Headers("Content-Type: application/json")
    @DELETE("transaction/{id}")
    Call<ApiResponseDto<Void>> deleteTransaction(@Path("id") long id);
}