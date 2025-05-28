package bd.edu.bubt.cse.fitrack.data.repository;

import android.content.Context;

import java.util.List;
import java.util.Map;

import bd.edu.bubt.cse.fitrack.data.api.RetrofitClient;
import bd.edu.bubt.cse.fitrack.data.api.TransactionApi;
import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.CreateTransactionRequest;
import bd.edu.bubt.cse.fitrack.data.dto.PaginatedTransactionResponse;
import bd.edu.bubt.cse.fitrack.data.dto.TransactionResponseWrapper;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionRepository {
    private final TransactionApi transactionApi;

    public TransactionRepository(Context context) {
        this.transactionApi = RetrofitClient.getTransactionApi(context);
    }

    public void getAllTransactions(
            int page,
            int size,
            String searchKey,
            String sortField,
            String sortDirec,
            String transactionType,
            TransactionCallback<PaginatedTransactionResponse> callback
    ) {
        transactionApi.getAllTransactions(page, size, searchKey, sortField, sortDirec, transactionType)
                .enqueue(new Callback<ApiResponseDto<TransactionResponseWrapper>>() {
                    @Override
                    public void onResponse(Call<ApiResponseDto<TransactionResponseWrapper>> call, Response<ApiResponseDto<TransactionResponseWrapper>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponseDto<TransactionResponseWrapper> apiResponse = response.body();

                            PaginatedTransactionResponse result = new PaginatedTransactionResponse(
                                    apiResponse.getResponse().getData(),
                                    apiResponse.getResponse().getTotalNoOfPages()
                            );

                            callback.onSuccess(result);
                        } else {
                            callback.onError("Failed to get transactions: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponseDto<TransactionResponseWrapper>> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }




    public void getTransactionById(long id, TransactionCallback<Transaction> callback) {
        transactionApi.getTransactionById(id).enqueue(new Callback<ApiResponseDto<Transaction>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Transaction>> call, Response<ApiResponseDto<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to get transaction");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Transaction>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createTransaction(CreateTransactionRequest transaction, TransactionCallback<String> callback) {
        transactionApi.createTransaction(transaction).enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to create transaction");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateTransaction(long id, Transaction transaction, TransactionCallback<Transaction> callback) {
        transactionApi.updateTransaction(id, transaction).enqueue(new Callback<ApiResponseDto<Transaction>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Transaction>> call, Response<ApiResponseDto<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to update transaction");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Transaction>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteTransaction(long id, TransactionCallback<Void> callback) {
        transactionApi.deleteTransaction(id).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to delete transaction");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface TransactionCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}