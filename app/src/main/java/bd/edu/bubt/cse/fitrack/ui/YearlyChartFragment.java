package bd.edu.bubt.cse.fitrack.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.edu.bubt.cse.fitrack.data.dto.YearlySummary;
import bd.edu.bubt.cse.fitrack.databinding.FragmentYearlyChartBinding;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ReportViewModel;
import lombok.NonNull;

public class YearlyChartFragment extends Fragment {
    private FragmentYearlyChartBinding binding;
    private ReportViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentYearlyChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class); // or use requireParentFragment() if scoped to parent
        setupYearSpinner();
        observeViewModel();
    }

    private void setupYearSpinner() {
        List<Integer> years = getYears();
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearSpinner.setAdapter(adapter);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        binding.yearSpinner.setSelection(years.indexOf(currentYear));

        binding.yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedYear = (int) parent.getItemAtPosition(position);
                viewModel.getYearlyBreakdown(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void observeViewModel() {
        viewModel.getYearlyBreakdownState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ReportViewModel.YearlySummaryState.Success) {
                List<YearlySummary> data = ((ReportViewModel.YearlySummaryState.Success) state).getData();
                if (data != null && !data.isEmpty()) {
                    setupBarChart(data);
                } else {
                    binding.barChart.clear();
                    Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
                }
            } else if (state instanceof ReportViewModel.YearlySummaryState.Error) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Integer> getYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int i = currentYear; i >= currentYear - 5; i--) {
            years.add(i);
        }
        return years;
    }

    private void setupBarChart(List<YearlySummary> data) {
        List<BarEntry> entriesIncome = new ArrayList<>();
        List<BarEntry> entriesExpense = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            YearlySummary item = data.get(i);
            entriesIncome.add(new BarEntry(i, item.getIncome().floatValue()));
            entriesExpense.add(new BarEntry(i, item.getExpense().floatValue()));
        }

        BarDataSet incomeDataSet = new BarDataSet(entriesIncome, "Income");
        incomeDataSet.setColor(Color.GREEN);

        BarDataSet expenseDataSet = new BarDataSet(entriesExpense, "Expense");
        expenseDataSet.setColor(Color.RED);

        BarData barData = new BarData(incomeDataSet, expenseDataSet);

        BarChart barChart = binding.barChart;
        barChart.setData(barData);

        // Define bar widths and spacing
        float groupSpace = 0.2f;
        float barSpace = 0.05f;
        float barWidth = 0.35f;

        // Set the bar width and group the bars
        barData.setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinimum(0f); // Important: Must be 0
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * data.size());

        barChart.groupBars(0f, groupSpace, barSpace); // Group the bars starting at x = 0

        // X-axis config
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);

        // Add year labels to X-axis
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < data.size()) {
                    return String.valueOf(data.get(index).getYear());
                } else {
                    return "";
                }
            }
        });

        // Y-axis config
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(100f);
        barChart.getAxisRight().setEnabled(false);

        // General chart appearance
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.setExtraOffsets(16f, 16f, 16f, 16f);

        // Animation
        barChart.animateY(1000);
        barChart.invalidate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
