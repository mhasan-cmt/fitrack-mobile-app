package bd.edu.bubt.cse.fitrack.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.function.Consumer;

import bd.edu.bubt.cse.fitrack.data.dto.CategoryChartSummary;
import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import bd.edu.bubt.cse.fitrack.data.dto.DailySummary;
import bd.edu.bubt.cse.fitrack.data.dto.MonthlySummary;
import bd.edu.bubt.cse.fitrack.data.dto.PredictionSummary;
import bd.edu.bubt.cse.fitrack.data.dto.TransactionsCountSummary;
import bd.edu.bubt.cse.fitrack.data.dto.YearlySummary;
import bd.edu.bubt.cse.fitrack.data.repository.ReportRepository;
import bd.edu.bubt.cse.fitrack.data.repository.ReportRepository.ReportCallback;
import lombok.Getter;

public class ReportViewModel extends AndroidViewModel {

    private final ReportRepository repository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<CategorySummaryState> reportState = new MutableLiveData<>();
    private final MutableLiveData<IncomeState> incomeState = new MutableLiveData<>();
    private final MutableLiveData<ExpenseState> expenseState = new MutableLiveData<>();
    private final MutableLiveData<MonthlySummaryState> monthlySummaryState = new MutableLiveData<>();
    private final MutableLiveData<CategoryBreakdownState> categoryBreakdownState = new MutableLiveData<>();
    private final MutableLiveData<DailyBreakdownState> dailyBreakdownState = new MutableLiveData<>();
    private final MutableLiveData<YearlySummaryState> yearlyBreakdownState = new MutableLiveData<>();
    private final MutableLiveData<TransactionsCountState> transactionCountSummaryState = new MutableLiveData<>();
    private final MutableLiveData<IncomePredictionSummaryState> incomePredictionSummaryState = new MutableLiveData<>();
    private final MutableLiveData<ExpensePredictionSummaryState> expensePredictionSummaryState = new MutableLiveData<>();
    private final MutableLiveData<CombinedFinanceState> combinedState = new MutableLiveData<>();

    public ReportViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ReportRepository(application);
        loadInitialData();
    }

    private void loadInitialData() {
        loadCategorySummaries();
    }

    // Generic repository call executor
    private <T> void executeRepositoryCall(
            RepositoryCall<T> call,
            Consumer<T> onSuccess,
            Consumer<String> onError
    ) {
        isLoading.setValue(true);
        call.execute(new ReportCallback<T>() {
            @Override
            public void onSuccess(T result) {
                isLoading.postValue(false);
                onSuccess.accept(result);
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                if (onError != null) {
                    onError.accept(errorMsg);
                }
            }
        });
    }

    // Type-specific executor for Double results
    private void executeDoubleRepositoryCall(
            RepositoryCall<Double> call,
            Consumer<Double> onSuccess,
            Consumer<String> onError
    ) {
        executeRepositoryCall(call, onSuccess, onError);
    }

    public void loadCategorySummaries() {
        executeRepositoryCall(
                repository::getMonthlySummaryByCategory,
                result -> reportState.postValue(new CategorySummaryState.Success(result)),
                error -> reportState.postValue(new CategorySummaryState.Error(error))
        );
    }

    public void getTotalIncome(int month, int year) {
        executeDoubleRepositoryCall(
                callback -> repository.getTotalIncomeOrExpense(2, month, year, callback),
                result -> incomeState.postValue(new IncomeState.Success(result != null ? result : 0.0)),
                error -> incomeState.postValue(new IncomeState.Error(error))
        );
    }

    public void getTotalExpense(int month, int year) {
        executeDoubleRepositoryCall(
                callback -> repository.getTotalIncomeOrExpense(1, month, year, callback),
                result -> expenseState.postValue(new ExpenseState.Success(result != null ? result : 0.0)),
                error -> expenseState.postValue(new ExpenseState.Error(error))
        );
    }

    public void getMonthlySummary(int year) {
        executeRepositoryCall(
                (ReportCallback<List<MonthlySummary>> callback) -> repository.getMonthlyIncomeExpenseData(year, callback),
                result -> monthlySummaryState.postValue(new MonthlySummaryState.Success(result)),
                error -> monthlySummaryState.postValue(new MonthlySummaryState.Error(error))
        );
    }


    public void getCategoryBreakdown(int month, int year) {
        executeRepositoryCall(
                (ReportCallback<List<CategoryChartSummary>> callback) -> repository.getCategoryBreakdownSummary(month, year, callback),
                result -> categoryBreakdownState.postValue(new CategoryBreakdownState.Success(result)),
                error -> categoryBreakdownState.postValue(new CategoryBreakdownState.Error(error))
        );
    }

    public void getDailyBreakdown(int month, int year) {
        executeRepositoryCall(
                (ReportCallback<List<DailySummary>> callback) -> repository.getDailySummary(month, year, callback),
                result -> dailyBreakdownState.postValue(new DailyBreakdownState.Success(result)),
                error -> dailyBreakdownState.postValue(new DailyBreakdownState.Error(error))
        );
    }

    public void getYearlyBreakdown(int year) {
        executeRepositoryCall(
                (ReportCallback<List<YearlySummary>> callback) -> repository.getYearlySummary(year, callback),
                result -> yearlyBreakdownState.postValue(new YearlySummaryState.Success(result)),
                error -> yearlyBreakdownState.postValue(new YearlySummaryState.Error(error))
        );
    }

    public void getTransactionCountSummary(int month, int year) {
        executeRepositoryCall(
                (ReportCallback<TransactionsCountSummary> callback) -> repository.getTransactionsCountSummary(month, year, callback),
                result -> transactionCountSummaryState.postValue(new TransactionsCountState.Success(result)),
                error -> transactionCountSummaryState.postValue(new TransactionsCountState.Error(error))
        );
    }

    public void getIncomePredictionSummary() {
        executeRepositoryCall(
                (ReportCallback<PredictionSummary> callback) -> repository.getPredictionSummary(2, callback),
                result -> incomePredictionSummaryState.postValue(new IncomePredictionSummaryState.Success(result)),
                error -> incomePredictionSummaryState.postValue(new IncomePredictionSummaryState.Error(error))
        );
    }

    public void getExpensePredictionSummary() {
        executeRepositoryCall(
                (ReportCallback<PredictionSummary> callback) -> repository.getPredictionSummary(1, callback),
                result -> expensePredictionSummaryState.postValue(new ExpensePredictionSummaryState.Success(result)),
                error -> expensePredictionSummaryState.postValue(new ExpensePredictionSummaryState.Error(error))
        );
    }


    public void loadCombinedFinanceData(int month, int year) {
        isLoading.setValue(true);
        getTotalIncome(month, year);
        getTotalExpense(month, year);
        getMonthlySummary(year);
        loadCategorySummaries();
    }

    // Getters
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<CategorySummaryState> getReportState() {
        return reportState;
    }

    public LiveData<IncomeState> getIncomeState() {
        return incomeState;
    }

    public LiveData<ExpenseState> getExpenseState() {
        return expenseState;
    }

    public LiveData<MonthlySummaryState> getMonthlySummaryState() {
        return monthlySummaryState;
    }

    public LiveData<CategoryBreakdownState> getCategoryBreakdownState() {
        return categoryBreakdownState;
    }

    public LiveData<DailyBreakdownState> getDailyBreakdownState() {
        return dailyBreakdownState;
    }

    public LiveData<YearlySummaryState> getYearlyBreakdownState() {
        return yearlyBreakdownState;
    }

    public LiveData<TransactionsCountState> getTransactionCountSummaryState() {
        return transactionCountSummaryState;
    }

    public LiveData<IncomePredictionSummaryState> getIncomePredictionSummaryState() {
        return incomePredictionSummaryState;
    }

    public LiveData<ExpensePredictionSummaryState> getExpensePredictionSummaryState() {
        return expensePredictionSummaryState;
    }

    public LiveData<CombinedFinanceState> getCombinedState() {
        return combinedState;
    }

    public interface RepositoryCall<T> {
        void execute(ReportCallback<T> callback);
    }

    public static abstract class CategorySummaryState {
        public static class Loading extends CategorySummaryState {
        }

        @Getter
        public static class Success extends CategorySummaryState {
            private final List<CategorySummary> summaries;

            public Success(List<CategorySummary> summaries) {
                this.summaries = summaries;
            }

        }

        @Getter
        public static class Error extends CategorySummaryState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

        }
    }

    public static abstract class IncomeState {
        public static class Loading extends IncomeState {
        }

        @Getter
        public static class Success extends IncomeState {
            private final double income;

            public Success(double income) {
                this.income = income;
            }

        }

        @Getter
        public static class Error extends IncomeState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

        }
    }

    public static abstract class ExpenseState {
        public static class Loading extends ExpenseState {
        }

        @Getter
        public static class Success extends ExpenseState {
            private final double expense;

            public Success(double expense) {
                this.expense = expense;
            }

        }

        @Getter
        public static class Error extends ExpenseState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

        }
    }

    public static abstract class MonthlySummaryState {
        @Getter
        public static class Success extends MonthlySummaryState {
            private final List<MonthlySummary> data;

            public Success(List<MonthlySummary> data) {
                this.data = data;
            }
        }

        @Getter
        public static class Error extends MonthlySummaryState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }
        }
    }

    public static abstract class CategoryBreakdownState {
        @Getter
        public static class Success extends CategoryBreakdownState {
            private final List<CategoryChartSummary> data;

            public Success(List<CategoryChartSummary> data) {
                this.data = data;
            }
        }

        @Getter
        public static class Error extends CategoryBreakdownState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }
        }
    }

    public static abstract class DailyBreakdownState {
        @Getter
        public static class Success extends DailyBreakdownState {
            private final List<DailySummary> data;

            public Success(List<DailySummary> data) {
                this.data = data;
            }
        }

        @Getter
        public static class Error extends DailyBreakdownState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }
        }
    }

    public static abstract class YearlySummaryState {
        @Getter
        public static class Success extends YearlySummaryState {
            private final List<YearlySummary> data;

            public Success(List<YearlySummary> data) {
                this.data = data;
            }
        }

        @Getter
        public static class Error extends YearlySummaryState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }
        }
    }

    public static abstract class TransactionsCountState {
        @Getter
        public static class Success extends TransactionsCountState {
            private final TransactionsCountSummary data;

            public Success(TransactionsCountSummary data) {
                this.data = data;
            }
        }

        @Getter
        public static class Error extends TransactionsCountState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }
        }
    }

    public static abstract class IncomePredictionSummaryState {
        @Getter
        public static class Success extends IncomePredictionSummaryState {
            private final PredictionSummary data;

            public Success(PredictionSummary data) {
                this.data = data;
            }
        }

        @Getter
        public static class Error extends IncomePredictionSummaryState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }
        }
    }

    public static abstract class ExpensePredictionSummaryState {
        @Getter
        public static class Success extends ExpensePredictionSummaryState {
            private final PredictionSummary data;

            public Success(PredictionSummary data) {
                this.data = data;
            }
        }

        @Getter
        public static class Error extends ExpensePredictionSummaryState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }
        }
    }

    public static abstract class CombinedFinanceState {
        @Getter
        public static class Success extends CombinedFinanceState {
            private final double income;
            private final double expense;
            private final List<CategorySummary> summaries;

            public Success(double income, double expense, List<CategorySummary> summaries) {
                this.income = income;
                this.expense = expense;
                this.summaries = summaries;
            }

        }

        @Getter
        public static class Error extends CombinedFinanceState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

        }

        public static class Loading extends CombinedFinanceState {
        }
    }
}