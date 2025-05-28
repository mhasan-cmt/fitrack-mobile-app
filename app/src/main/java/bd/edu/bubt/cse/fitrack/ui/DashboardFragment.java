package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import bd.edu.bubt.cse.fitrack.data.dto.ProfileResponse;
import bd.edu.bubt.cse.fitrack.ui.adapter.CategorySummaryAdapter;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ProfileViewModel;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ReportViewModel;

public class DashboardFragment extends Fragment {

    private ReportViewModel viewModel;
    private RecyclerView rvCategorySummary;
    private CategorySummaryAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        // Init UI
        progressBar = root.findViewById(R.id.progress_bar);
        tvError = root.findViewById(R.id.tv_error_message);
        rvCategorySummary = root.findViewById(R.id.rv_category_summary);

        rvCategorySummary.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new CategorySummaryAdapter(new ArrayList<>());
        rvCategorySummary.setAdapter(adapter);

        observeViewModel();

        return root;
    }

    private void observeViewModel() {
        viewModel.getReportState().observe(getViewLifecycleOwner(), summaries -> {
            if (summaries instanceof ReportViewModel.CategorySummaryState.Success) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.GONE);
                List<CategorySummary> result = ((ReportViewModel.CategorySummaryState.Success) summaries).getSummaries();
                if (result != null) {
                    adapter.setCategoryList(result);
                }
            }else if (summaries instanceof ReportViewModel.CategorySummaryState.Error) {
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
    }
}

