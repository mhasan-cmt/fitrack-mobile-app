package bd.edu.bubt.cse.fitrack.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import bd.edu.bubt.cse.fitrack.ui.adapter.CategorySummaryAdapter;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ReportViewModel;

public class DashboardFragment extends Fragment {

    private ReportViewModel viewModel;
    private RecyclerView rvCategorySummary;
    private CategorySummaryAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvError;

    private double totalIncome, totalExpense;

    private TextView tvTotalIncome, tvTotalExpense, tvNetSavings, tvExpensePercentage;

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


        // Init UI
        progressBar = root.findViewById(R.id.progress_bar);
        tvError = root.findViewById(R.id.tv_error_message);
        tvTotalIncome = root.findViewById(R.id.tv_total_income);
        tvTotalExpense = root.findViewById(R.id.tv_total_expense);
        tvNetSavings = root.findViewById(R.id.tv_net_savings);
        tvExpensePercentage = root.findViewById(R.id.tv_expense_percentage);

        rvCategorySummary = root.findViewById(R.id.rv_category_summary);
        spinnerMonth = root.findViewById(R.id.spinner_month);
        spinnerYear = root.findViewById(R.id.spinner_year);

        totalExpense = 0.0;
        totalIncome = 0.0;

        rvCategorySummary.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new CategorySummaryAdapter(new ArrayList<>());
        rvCategorySummary.setAdapter(adapter);

        setupSpinners();

        observeViewModel();

        return root;
    }

    private void updateNetSavings() {
        double netSavings = totalIncome - totalExpense;
        animateTextChange(tvNetSavings, String.format("৳%.2f", netSavings));
        tvNetSavings.setTextColor(netSavings >= 0 ? Color.GREEN : Color.RED);
    }

    private void updateExpensePercentage() {
        if (totalIncome > 0) {
            double percent = totalIncome > 0 ? (totalExpense / totalIncome) * 100 : 0;
            tvExpensePercentage.setText(String.format("(%.1f%%)", percent));
        } else {
            tvExpensePercentage.setText("");
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



    private void setupSpinners() {
        // Month names
        String[] months = new DateFormatSymbols().getMonths();
        List<String> monthList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            monthList.add(months[i]);
        }

        // Year list - last 5 years including current
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            years.add(currentYear - i);
        }

        // Set Adapters
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, monthList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Default selections
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-based
        selectedYear = currentYear;

        spinnerMonth.setSelection(selectedMonth);
        spinnerYear.setSelection(0); // current year is first in the list

        // Listeners
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int newMonth = spinnerMonth.getSelectedItemPosition() + 1;
                int newYear = (int) spinnerYear.getSelectedItem();


                if (!isInitialSelection &&
                        (newMonth != selectedMonth || newYear != selectedYear)) {

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

        spinnerMonth.setOnItemSelectedListener(listener);
        spinnerYear.setOnItemSelectedListener(listener);

        // First-time load
        fetchData();
    }

    private void fetchData() {
        viewModel.getTotalExpense(selectedMonth, selectedYear);
        viewModel.getTotalIncome(selectedMonth, selectedYear);
    }

    private void observeViewModel() {
        viewModel.getReportState().observe(getViewLifecycleOwner(), summaries -> {
            if (summaries instanceof ReportViewModel.CategorySummaryState.Success) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                List<CategorySummary> result = ((ReportViewModel.CategorySummaryState.Success) summaries).getSummaries();
                adapter.setCategoryList(result != null ? result : new ArrayList<>());
            } else if (summaries instanceof ReportViewModel.CategorySummaryState.Error) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Failed to load category summaries");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                tvError.setText(errorMsg);
                tvError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getIncomeState().observe(getViewLifecycleOwner(), incomeState -> {
            if (incomeState instanceof ReportViewModel.IncomeState.Success) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                totalIncome = ((ReportViewModel.IncomeState.Success) incomeState).getIncome();
                animateTextChange(tvTotalIncome, String.format("Total Income: ৳%.2f", totalIncome));
                updateNetSavings();
                updateExpensePercentage();
            } else if (incomeState instanceof ReportViewModel.IncomeState.Error) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Failed to load income state");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getExpenseState().observe(getViewLifecycleOwner(), expenseState -> {
            if (expenseState instanceof ReportViewModel.ExpenseState.Success) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                totalExpense = ((ReportViewModel.ExpenseState.Success) expenseState).getExpense();
                animateTextChange(tvTotalExpense, String.format("Total Expense: ৳%.2f", totalExpense));
                updateNetSavings();
                updateExpensePercentage();
            } else if (expenseState instanceof ReportViewModel.ExpenseState.Error) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Failed to load expense state");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.GONE);
            }
        });

    }
}


