package bd.edu.bubt.cse.fitrack.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CategoryChartSummary;
import bd.edu.bubt.cse.fitrack.data.dto.DailySummary;
import bd.edu.bubt.cse.fitrack.data.dto.MonthlySummary;
import bd.edu.bubt.cse.fitrack.databinding.FragmentCategoryBreakdownBinding;
import bd.edu.bubt.cse.fitrack.databinding.FragmentDailyExpenseBinding;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ReportViewModel;

public class DailyExpenseFragment extends Fragment {
    private FragmentDailyExpenseBinding binding;
    private ReportViewModel viewModel;
    private int selectedMonth;
    private int selectedYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDailyExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);

        setupDayMonthSpinners();
        setupObservers();
    }

    private void setupDayMonthSpinners() {
        // Month spinner setup
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.months_array,
                android.R.layout.simple_spinner_item
        );
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.monthSpinner.setAdapter(monthAdapter);

        // Year spinner setup
        List<Integer> years = getYears();
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                years
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearSpinner.setAdapter(yearAdapter);

        // Set current month/year
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH) + 1; // 1-based

        binding.monthSpinner.setSelection(calendar.get(Calendar.MONTH));
        binding.yearSpinner.setSelection(years.indexOf(selectedYear));

        // Spinner listeners
        binding.monthSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position + 1;
                loadData();
            }
        });

        binding.yearSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = (int) parent.getItemAtPosition(position);
                loadData();
            }
        });
    }

    private void setupObservers() {
        viewModel.getDailyBreakdownState().observe(getViewLifecycleOwner(), state -> {
            binding.progressBar.setVisibility(View.GONE);

            if (state instanceof ReportViewModel.DailyBreakdownState.Success) {
                List<DailySummary> dailyData = ((ReportViewModel.DailyBreakdownState.Success) state).getData();
                if (dailyData.isEmpty()) {
                    showEmptyState();
                } else {
                    showDailyBarChart(dailyData);
                }
            } else if (state instanceof ReportViewModel.DailyBreakdownState.Error) {
                Toast.makeText(requireContext(),
                        ((ReportViewModel.DailyBreakdownState.Error) state).getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getDailyBreakdown(selectedMonth, selectedYear);
    }

    private void showEmptyState() {
        binding.emptyStateText.setText("No data available for the selected month.");
        binding.emptyStateText.setVisibility(View.VISIBLE);
        binding.barChart.setVisibility(View.GONE);
    }

    private void showDailyBarChart(List<DailySummary> data) {
        binding.emptyStateText.setVisibility(View.GONE);
        binding.barChart.setVisibility(View.VISIBLE);

        List<BarEntry> expenseEntries = new ArrayList<>();
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<String> days = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            DailySummary summary = data.get(i);
            expenseEntries.add(new BarEntry(i, summary.getExpense().floatValue()));
            incomeEntries.add(new BarEntry(i, summary.getIncome().floatValue()));
            days.add(String.valueOf(summary.getDay()));
        }

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expenses");
        expenseDataSet.setColor(Color.RED);

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.GREEN);

        BarData barData = new BarData(expenseDataSet, incomeDataSet);
        barData.setBarWidth(0.4f);

        binding.barChart.setData(barData);

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setDrawGridLines(false);

        binding.barChart.getAxisLeft().setDrawGridLines(false);
        binding.barChart.getAxisRight().setEnabled(false);

        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setFitBars(true);
        binding.barChart.invalidate();
    }

    private List<Integer> getYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int i = currentYear; i >= currentYear - 10; i--) {
            years.add(i);
        }
        return years;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private abstract class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }
}

