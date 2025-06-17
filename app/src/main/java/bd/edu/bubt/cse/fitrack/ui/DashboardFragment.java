package bd.edu.bubt.cse.fitrack.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import bd.edu.bubt.cse.fitrack.ui.adapter.CategorySummaryAdapter;
import bd.edu.bubt.cse.fitrack.ui.notification.SpendingNotificationWorker;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ReportViewModel;

public class DashboardFragment extends Fragment {

    private ReportViewModel viewModel;
    private RecyclerView rvCategorySummary;
    private CategorySummaryAdapter adapter;
    private ProgressBar progressBar, progressExpenseRatio;
    private TextView tvError, tvProgressLabel;
    private double totalIncome = 0.0, totalExpense = 0.0;

    private TextView tvTotalIncome, tvTotalExpense, tvNetSavings;
    private Spinner spinnerMonth, spinnerYear;

    private int selectedMonth, selectedYear;
    private boolean isInitialSelection = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        initializeViews(root);
        setupRecyclerView();
        setupSpinners();
        observeViewModel();
        return root;
    }

    private void initializeViews(View root) {
        progressBar = root.findViewById(R.id.progress_bar);
        tvError = root.findViewById(R.id.tv_error_message);
        tvTotalIncome = root.findViewById(R.id.tv_total_income);
        tvTotalExpense = root.findViewById(R.id.tv_total_expense);
        tvNetSavings = root.findViewById(R.id.tv_net_savings);
        progressExpenseRatio = root.findViewById(R.id.progress_expense_ratio);
        tvProgressLabel = root.findViewById(R.id.tv_progress_label);

        rvCategorySummary = root.findViewById(R.id.rv_category_summary);
        spinnerMonth = root.findViewById(R.id.spinner_month);
        spinnerYear = root.findViewById(R.id.spinner_year);
    }

    private void setupRecyclerView() {
        rvCategorySummary.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new CategorySummaryAdapter(new ArrayList<>());
        rvCategorySummary.setAdapter(adapter);
    }

    private void setupSpinners() {
        // Month
        String[] months = new DateFormatSymbols().getMonths();
        List<String> monthList = Arrays.asList(Arrays.copyOf(months, 12));
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, monthList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Year
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> yearList = new ArrayList<>();
        for (int i = 0; i < 5; i++) yearList.add(currentYear - i);
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        selectedMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-based
        selectedYear = currentYear;
        spinnerMonth.setSelection(selectedMonth);
        spinnerYear.setSelection(0); // First item is current year

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int newMonth = spinnerMonth.getSelectedItemPosition() + 1;
                int newYear = (int) spinnerYear.getSelectedItem();
                if (!isInitialSelection && (newMonth != selectedMonth || newYear != selectedYear)) {
                    selectedMonth = newMonth;
                    selectedYear = newYear;
                    fetchData();
                }
                isInitialSelection = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spinnerMonth.setOnItemSelectedListener(spinnerListener);
        spinnerYear.setOnItemSelectedListener(spinnerListener);

        fetchData(); // Initial data load
    }

    private void fetchData() {
        viewModel.getTotalIncome(selectedMonth, selectedYear);
        viewModel.getTotalExpense(selectedMonth, selectedYear);
    }

    private void observeViewModel() {
        viewModel.getReportState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ReportViewModel.CategorySummaryState.Success) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                List<CategorySummary> data = ((ReportViewModel.CategorySummaryState.Success) state).getSummaries();
                adapter.setCategoryList(data != null ? data : new ArrayList<>());
            } else if (state instanceof ReportViewModel.CategorySummaryState.Error) {
                showError("Failed to load category summaries");
            } else {
                showLoading();
            }
        });

        viewModel.getIncomeState().observe(getViewLifecycleOwner(), incomeState -> {
            if (incomeState instanceof ReportViewModel.IncomeState.Success) {
                totalIncome = ((ReportViewModel.IncomeState.Success) incomeState).getIncome();
                animateTextChange(tvTotalIncome, String.format("Earnings: ৳%.2f", totalIncome));
                updateNetSavings();
                updateExpensePercentage();
            } else if (incomeState instanceof ReportViewModel.IncomeState.Error) {
                showError("Failed to load income");
            }
        });

        viewModel.getExpenseState().observe(getViewLifecycleOwner(), expenseState -> {
            if (expenseState instanceof ReportViewModel.ExpenseState.Success) {
                totalExpense = ((ReportViewModel.ExpenseState.Success) expenseState).getExpense();
                animateTextChange(tvTotalExpense, String.format("Spendings: ৳%.2f", totalExpense));
                updateNetSavings();
                updateExpensePercentage();
            } else if (expenseState instanceof ReportViewModel.ExpenseState.Error) {
                showError("Failed to load expense");
            }
        });

        sendOverspendingNotification();

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (!TextUtils.isEmpty(error)) {
                showError(error);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });
    }

    private void updateNetSavings() {
        double netSavings = totalIncome - totalExpense;
        animateTextChange(tvNetSavings, String.format("৳%.2f", netSavings));
        tvNetSavings.setTextColor(netSavings >= 0 ? Color.GREEN : Color.RED);
        SharedPreferences prefs = requireContext().getSharedPreferences("finance_prefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putFloat("latest_income", (float) totalIncome)
                .putFloat("latest_expense", (float) totalExpense)
                .apply();
    }

    private void sendOverspendingNotification() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SpendingNotificationWorker.class).build();
        WorkManager.getInstance(requireContext()).enqueue(workRequest);
    }


    private void updateExpensePercentage() {
        if (totalIncome > 0) {
            double percent = (totalExpense / totalIncome) * 100;

            int progress = (int) Math.min(100, percent);
            ObjectAnimator.ofInt(progressExpenseRatio, "progress", progress).setDuration(500).start();

            if (percent > 100) {
                tvProgressLabel.setText("100%");
                tvProgressLabel.setTextColor(Color.RED);
                Toast.makeText(getContext(), "You're spending more than you earn!", Toast.LENGTH_LONG).show();
            } else {
                tvProgressLabel.setText(String.format("%.1f%%", percent));
                tvProgressLabel.setTextColor(Color.BLACK);
            }

        } else {
            progressExpenseRatio.setProgress(0);
            tvProgressLabel.setText("0%");
            tvProgressLabel.setTextColor(Color.BLACK);
        }
    }


    private void animateTextChange(TextView textView, String newText) {
        textView.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {
                    textView.setText(newText);
                    textView.animate().alpha(1f).setDuration(150).start();
                }).start();
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }
}



