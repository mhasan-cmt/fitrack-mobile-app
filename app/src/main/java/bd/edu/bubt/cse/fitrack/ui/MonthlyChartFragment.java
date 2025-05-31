package bd.edu.bubt.cse.fitrack.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.MonthlySummary;
import bd.edu.bubt.cse.fitrack.databinding.FragmentMonthlyChartBinding;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ReportViewModel;

public class MonthlyChartFragment extends Fragment {
    private FragmentMonthlyChartBinding binding;
    private ReportViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMonthlyChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);

        setupYearSpinner();
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
                viewModel.getMonthlySummary(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        viewModel.getMonthlySummaryState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ReportViewModel.MonthlySummaryState.Success) {
                List<MonthlySummary> data = ((ReportViewModel.MonthlySummaryState.Success) state).getData();
                setupBarChart(data);
            } else if (state instanceof ReportViewModel.MonthlySummaryState.Error) {
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

    private void setupBarChart(List<MonthlySummary> data) {
        List<BarEntry> entriesIncome = new ArrayList<>();
        List<BarEntry> entriesExpense = new ArrayList<>();

        for (MonthlySummary item : data) {
            float monthIndex = item.getMonth(); // Assuming 0-based index
            entriesIncome.add(new BarEntry(monthIndex, (float) item.getIncome()));
            entriesExpense.add(new BarEntry(monthIndex, (float) item.getExpense()));
        }

        BarDataSet incomeDataSet = new BarDataSet(entriesIncome, "Income");
        incomeDataSet.setColor(Color.GREEN);

        BarDataSet expenseDataSet = new BarDataSet(entriesExpense, "Expense");
        expenseDataSet.setColor(Color.RED);

        BarData barData = new BarData(incomeDataSet, expenseDataSet);

        // Configure chart appearance
        BarChart barChart = binding.barChart;
        barChart.setData(barData);

        // X-axis configuration
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(11.5f); // For 12 months (0-11)
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getMonths()));

        // Y-axis configuration
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(100f); // Adjust based on your data range

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Chart general settings
        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.setExtraOffsets(16f, 16f, 16f, 16f); // Add padding

        // Group bars
        float groupSpace = 0.4f;
        float barSpace = 0.05f;
        float barWidth = 0.275f;

        barData.setBarWidth(barWidth);
        barChart.groupBars(0f, groupSpace, barSpace);

        // Animation
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private String[] getMonths() {
        return new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

