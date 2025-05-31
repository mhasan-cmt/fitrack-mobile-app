package bd.edu.bubt.cse.fitrack.ui;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CategoryChartSummary;
import bd.edu.bubt.cse.fitrack.databinding.FragmentCategoryBreakdownBinding;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ReportViewModel;

public class CategoryBreakdownFragment extends Fragment {
    private FragmentCategoryBreakdownBinding binding;
    private ReportViewModel viewModel;
    private int selectedMonth;
    private int selectedYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBreakdownBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);

        setupMonthYearSpinners();
        setupChartConfiguration();
        setupObservers();
    }

    private void setupMonthYearSpinners() {
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
        selectedMonth = calendar.get(Calendar.MONTH); // 0-based

        binding.monthSpinner.setSelection(selectedMonth);
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

    private void setupChartConfiguration() {
        PieChart pieChart = binding.pieChart;

        // Basic chart configuration
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(24f, 24f, 24f, 24f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        // Center hole configuration
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setHoleColor(Color.TRANSPARENT);

        // Rotation and highlight
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // Legend customization
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(12f);
        legend.setYEntrySpace(8f);
        legend.setYOffset(16f);

        // Entry label styling
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
    }

    private void setupObservers() {
        viewModel.getCategoryBreakdownState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ReportViewModel.CategoryBreakdownState.Success) {
                binding.progressBar.setVisibility(View.GONE);
                binding.pieChart.setVisibility(View.VISIBLE);

                List<CategoryChartSummary> data = ((ReportViewModel.CategoryBreakdownState.Success) state).getData();
                if (data.isEmpty()) {
                    showEmptyState();
                } else {
                    setupPieChart(data);
                }
            } else if (state instanceof ReportViewModel.CategoryBreakdownState.Error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(),
                        ((ReportViewModel.CategoryBreakdownState.Error) state).getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        viewModel.getCategoryBreakdown(selectedMonth, selectedYear);
    }

    private void setupPieChart(List<CategoryChartSummary> data) {
        List<PieEntry> entries = new ArrayList<>();
        int[] colors = new int[data.size()];

        for (int i = 0; i < data.size(); i++) {
            CategoryChartSummary item = data.get(i);
            entries.add(new PieEntry((float) item.getTotal(), item.getCategory()));
            colors[i] = Color.rgb(
                    (int) (Math.random() * 256),
                    (int) (Math.random() * 256),
                    (int) (Math.random() * 256)
            );
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueLinePart1OffsetPercentage(80f);
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueFormatter(new PercentFormatter(binding.pieChart));



        binding.pieChart.setData(pieData);
        binding.pieChart.setCenterText(generateCenterText(data));
        binding.pieChart.animateY(1000, Easing.EaseInOutQuad);
        binding.pieChart.invalidate();
    }

    private SpannableString generateCenterText(List<CategoryChartSummary> data) {
        double total = data.stream().mapToDouble(CategoryChartSummary::getTotal).sum();
        String monthName = getResources().getStringArray(R.array.months_array)[selectedMonth - 1];

        SpannableString s = new SpannableString(monthName + "\n" +
                NumberFormat.getCurrencyInstance().format(total));
        s.setSpan(new RelativeSizeSpan(1.2f), 0, monthName.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, monthName.length(), 0);
        return s;
    }

    private void showEmptyState() {
        binding.pieChart.setVisibility(View.GONE);
        binding.emptyStateText.setVisibility(View.VISIBLE);
        binding.emptyStateText.setText("No data available for " +
                getResources().getStringArray(R.array.months_array)[selectedMonth - 1] + " " + selectedYear);
    }

    private List<Integer> getYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> years = new ArrayList<>();
        for (int i = currentYear; i >= currentYear - 5; i--) {
            years.add(i);
        }
        return years;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Helper class for spinner listeners
    private abstract class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }
}
