package bd.edu.bubt.cse.fitrack.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.repository.TransactionRepository;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;

public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository transactionRepository;
    private final MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();
    private final MutableLiveData<Transaction> selectedTransaction = new MutableLiveData<>();
    private final MutableLiveData<TransactionState> transactionState = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactionRepository = new TransactionRepository(application);
    }

    public LiveData<List<Transaction>> getTransactions() {
        return transactions;
    }

    public LiveData<Transaction> getSelectedTransaction() {
        return selectedTransaction;
    }

    public LiveData<TransactionState> getTransactionState() {
        return transactionState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadTransactions(int page, int size) {
        isLoading.setValue(true);
        transactionRepository.getAllTransactions(page, size, new TransactionRepository.TransactionCallback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> result) {
                isLoading.postValue(false);
                transactions.postValue(result);
                transactionState.postValue(new TransactionState.Success());
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                transactionState.postValue(new TransactionState.Error(errorMsg));
            }
        });
    }

    public void getTransactionById(long id) {
        isLoading.setValue(true);
        transactionRepository.getTransactionById(id, new TransactionRepository.TransactionCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                isLoading.postValue(false);
                selectedTransaction.postValue(result);
                transactionState.postValue(new TransactionState.Success());
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                transactionState.postValue(new TransactionState.Error(errorMsg));
            }
        });
    }

    public void createTransaction(Transaction transaction) {
        isLoading.setValue(true);
        transactionRepository.createTransaction(transaction, new TransactionRepository.TransactionCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                isLoading.postValue(false);
                selectedTransaction.postValue(result);
                transactionState.postValue(new TransactionState.Success());
                // Reload transactions to reflect the new transaction
                loadTransactions(0, 10);
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                transactionState.postValue(new TransactionState.Error(errorMsg));
            }
        });
    }

    public void updateTransaction(long id, Transaction transaction) {
        isLoading.setValue(true);
        transactionRepository.updateTransaction(id, transaction, new TransactionRepository.TransactionCallback<Transaction>() {
            @Override
            public void onSuccess(Transaction result) {
                isLoading.postValue(false);
                selectedTransaction.postValue(result);
                transactionState.postValue(new TransactionState.Success());
                // Reload transactions to reflect the updated transaction
                loadTransactions(0, 10);
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                transactionState.postValue(new TransactionState.Error(errorMsg));
            }
        });
    }

    public void deleteTransaction(long id) {
        isLoading.setValue(true);
        transactionRepository.deleteTransaction(id, new TransactionRepository.TransactionCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.postValue(false);
                transactionState.postValue(new TransactionState.Success());
                // Reload transactions to reflect the deleted transaction
                loadTransactions(0, 10);
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                transactionState.postValue(new TransactionState.Error(errorMsg));
            }
        });
    }

    // State classes for transaction operations
    public static abstract class TransactionState {

        public static class Loading extends TransactionState {
            // Empty class
        }

        public static class Success extends TransactionState {
            // Empty class
        }

        public static class Error extends TransactionState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }
        }
    }
}