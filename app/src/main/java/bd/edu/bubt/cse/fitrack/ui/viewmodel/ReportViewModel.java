package bd.edu.bubt.cse.fitrack.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import bd.edu.bubt.cse.fitrack.data.repository.ReportRepository;

public class ReportViewModel extends AndroidViewModel {

    private final ReportRepository repository;
    private final MutableLiveData<Double> getTotalIncome = new MutableLiveData<>();
    private final MutableLiveData<Double> getTotalExpense = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<CategorySummaryState> reportState = new MutableLiveData<>();

    public ReportViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ReportRepository(application);
        loadCategorySummaries();
    }

    private void loadCategorySummaries() {
        isLoading.setValue(true);
        reportState.setValue(new CategorySummaryState.Loading());

        repository.getMonthlySummaryByCategory(new ReportRepository.ReportCallback<List<CategorySummary>>() {
            @Override
            public void onSuccess(List<CategorySummary> result) {
                isLoading.postValue(false);
                reportState.postValue(new CategorySummaryState.Success(result));
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                reportState.postValue(new CategorySummaryState.Error(errorMsg));
            }
        });
    }

    public void getTotalIncome(int month, int year) {
        isLoading.setValue(true);
        repository.getTotalIncomeOrExpense(1, month, year, new ReportRepository.ReportCallback<List<Double>>() {
            @Override
            public void onSuccess(List<Double> result) {
                isLoading.postValue(false);
                if (!result.isEmpty()) {
                    getTotalIncome.postValue(result.get(0));
                } else {
                    getTotalIncome.postValue(0.0);
                }
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
            }
        });
    }

    public void getTotalExpense(int month, int year) {
        isLoading.setValue(true);
        repository.getTotalIncomeOrExpense(2, month, year, new ReportRepository.ReportCallback<List<Double>>() {
            @Override
            public void onSuccess(List<Double> result) {
                isLoading.postValue(false);
                if (!result.isEmpty()) {
                    getTotalExpense.postValue(result.get(0));
                } else {
                    getTotalExpense.postValue(0.0);
                }
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
            }
        });
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Double> getTotalIncome() {
        return getTotalIncome;
    }

    public LiveData<Double> getTotalExpense() {
        return getTotalExpense;
    }

    public LiveData<CategorySummaryState> getReportState() {
        return reportState;
    }

    // UI State representation
    public static abstract class CategorySummaryState {
        public static class Loading extends CategorySummaryState {
        }

        public static class Success extends CategorySummaryState {
            private final List<CategorySummary> summaries;

            public Success(List<CategorySummary> summaries) {
                this.summaries = summaries;
            }

            public List<CategorySummary> getSummaries() {
                return summaries;
            }
        }

        public static class Error extends CategorySummaryState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }
        }
    }

    public static abstract class IncomeState {
        public static class Loading extends CategorySummaryState {
        }

        public static class Success extends CategorySummaryState {
            private final double income;

            public Success(double income) {
                this.income = income;
            }

            public double getIncome() {
                return income;
            }
        }

        public static class Error extends CategorySummaryState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }
        }
    }

    public static abstract class ExpenseState {
        public static class Loading extends CategorySummaryState {
        }

        public static class Success extends CategorySummaryState {
            private final double expense;

            public Success(double expense) {
                this.expense = expense;
            }

            public double getExpense() {
                return expense;
            }
        }

        public static class Error extends CategorySummaryState {
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

