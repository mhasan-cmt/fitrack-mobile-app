package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.ui.adapter.TransactionAdapter;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.TransactionViewModel;

public class TransactionsFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private FloatingActionButton fabAddTransaction;
    private SearchView searchView;
    private Spinner spinnerSortField;
    private ImageButton btnSortDirection;
    private ProgressBar progressBar;
    private TextView tvError;
    private TextView tvPageNumber;
    private Button btnPrev, btnNext;

    private boolean isAscending = true;
    private String currentSortField = "date";
    private String currentSearchQuery = "";
    private String transactionType = "";

    private int pageSize = 5;
    private int currentPage = 0;
    private int totalPages = 1;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    private TransactionViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transactions, container, false);

        rvTransactions = root.findViewById(R.id.rv_transactions);
        fabAddTransaction = root.findViewById(R.id.fab_add_transaction);
        searchView = root.findViewById(R.id.search_view);
        spinnerSortField = root.findViewById(R.id.spinner_sort_field);
        btnSortDirection = root.findViewById(R.id.btn_sort_direction);
        tvError = root.findViewById(R.id.tv_error_message);
        progressBar = root.findViewById(R.id.progress_bar);
        tvPageNumber = root.findViewById(R.id.tv_page_number);
        btnPrev = root.findViewById(R.id.btn_prev);
        btnNext = root.findViewById(R.id.btn_next);

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        rvTransactions.setAdapter(transactionAdapter);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        setupSortSpinner();
        setupSortDirectionButton();
        setupSearchView();
        setupFab();
        setupPaginationButtons();

        observeViewModel();
        loadPage(0);

        return root;
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_fields_array,
                android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortField.setAdapter(sortAdapter);
        spinnerSortField.setSelection(0);
        spinnerSortField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortField = parent.getItemAtPosition(position).toString().toLowerCase();
                resetAndLoad();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupSortDirectionButton() {
        btnSortDirection.setOnClickListener(v -> {
            isAscending = !isAscending;
            btnSortDirection.setImageResource(isAscending ? R.drawable.ic_budget : R.drawable.ic_person);
            resetAndLoad();
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query.trim();
                resetAndLoad();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText.trim();
                resetAndLoad();
                return true;
            }
        });
    }

    private void setupFab() {
        fabAddTransaction.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new AddTransactionFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupPaginationButtons() {
        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                loadPage(currentPage - 1);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (!isLastPage && currentPage + 1 < totalPages) {
                loadPage(currentPage + 1);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (currentPage == 0) {
                transactionAdapter.clear();
            }

            if (transactions != null && !transactions.isEmpty()) {
                transactionAdapter.setTransactions(transactions);
                isLastPage = transactions.size() < pageSize;
            } else {
                isLastPage = true;
                transactionAdapter.clear();
            }

            scrollToTop();

            updatePaginationButtons();

            isLoading = false;
            progressBar.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            isLoading = loading != null && loading;
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                tvError.setText(errorMsg);
                tvError.setVisibility(View.VISIBLE);
                isLoading = false;
                progressBar.setVisibility(View.GONE);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getTotalPages().observe(getViewLifecycleOwner(), total -> {
            if (total != null) {
                totalPages = total;
                updatePaginationButtons();
            }
        });
    }

    private void updatePaginationButtons() {
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(!isLastPage && currentPage + 1 < totalPages);

        tvPageNumber.setText("Page " + (currentPage + 1) + " / " + totalPages);
    }

    private void scrollToTop() {
        rvTransactions.scrollToPosition(0);
    }

    private void resetAndLoad() {
        currentPage = 0;
        isLastPage = false;
        transactionAdapter.clear();
        loadPage(currentPage);
    }

    private void loadPage(int page) {
        currentPage = page;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        String sortDirection = isAscending ? "asc" : "desc";

        viewModel.loadTransactions(page, pageSize, currentSearchQuery, currentSortField, sortDirection, transactionType);
    }
}





