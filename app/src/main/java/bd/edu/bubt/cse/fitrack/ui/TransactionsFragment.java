package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import bd.edu.bubt.cse.fitrack.ui.adapter.TransactionAdapter;

public class TransactionsFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private FloatingActionButton fabAddTransaction;
    private SearchView searchView;
    private Spinner spinnerSortField;
    private ImageButton btnSortDirection;

    private boolean isLoading = false;
    private int pageSize = 5;
    private int currentPage = 0;
    private boolean isAscending = true;
    private String currentSortField = "date"; // default sort field

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transactions, container, false);

        rvTransactions = root.findViewById(R.id.rv_transactions);
        fabAddTransaction = root.findViewById(R.id.fab_add_transaction);
        searchView = root.findViewById(R.id.search_view);
        spinnerSortField = root.findViewById(R.id.spinner_sort_field);
        btnSortDirection = root.findViewById(R.id.btn_sort_direction);

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        // FAB click
        fabAddTransaction.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new AddTransactionFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Setup adapter
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        rvTransactions.setAdapter(transactionAdapter);

        // Setup sort spinner
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_fields_array, // define in strings.xml
                android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortField.setAdapter(sortAdapter);
        spinnerSortField.setSelection(0); // default selection
        spinnerSortField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortField = parent.getItemAtPosition(position).toString().toLowerCase();
                sortAndReload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Sort direction toggle
        btnSortDirection.setOnClickListener(v -> {
            isAscending = !isAscending;
            btnSortDirection.setImageResource(isAscending ? R.drawable.ic_setting : R.drawable.ic_setting);
            sortAndReload();
        });

        // Search logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                transactionAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionAdapter.filter(newText);
                return true;
            }
        });

        // Pagination listener
        rvTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + 2)) {
                    isLoading = true;
                    loadNextPage();
                }
            }
        });

        // Initial load
        loadAllTransactions();
        sortAndReload();

        return root;
    }

    private void sortAndReload() {
        Comparator<Transaction> comparator;
        switch (currentSortField) {
            case "amount":
                comparator = Comparator.comparingDouble(Transaction::getAmount);
                break;
            case "category":
                comparator = Comparator.comparing(Transaction::getCategoryName);
                break;
            default:
                comparator = Comparator.comparing(Transaction::getDate);
                break;
        }
        if (!isAscending) {
            comparator = comparator.reversed();
        }

        transactionList.sort(comparator);
        transactionAdapter.clear();
        currentPage = 0;
        loadNextPage();
    }

    private void loadAllTransactions() {
        transactionList.clear();
        transactionList.add(new Transaction(1L, 101, "Groceries", 0, "Bought groceries", -45.99, LocalDate.of(2025, 3, 15), "user@example.com"));
        transactionList.add(new Transaction(2L, 102, "Income", 1, "Monthly salary", 1200.00, LocalDate.of(2025, 3, 10), "user@example.com"));
        transactionList.add(new Transaction(3L, 103, "Utilities", 0, "Paid electric bill", -75.50, LocalDate.of(2025, 3, 8), "user@example.com"));
        transactionList.add(new Transaction(4L, 104, "Dining", 0, "Dinner at a restaurant", -30.25, LocalDate.of(2025, 3, 5), "user@example.com"));
        transactionList.add(new Transaction(5L, 105, "Freelance", 1, "Freelance project payment", 500.00, LocalDate.of(2025, 3, 2), "user@example.com"));
        transactionList.add(new Transaction(6L, 101, "Groceries", 0, "Weekly grocery shopping", -65.75, LocalDate.of(2025, 2, 28), "user@example.com"));
        transactionList.add(new Transaction(7L, 106, "Transportation", 0, "Gas refill", -40.00, LocalDate.of(2025, 2, 25), "user@example.com"));
        transactionList.add(new Transaction(8L, 107, "Entertainment", 0, "Movie tickets", -25.00, LocalDate.of(2025, 2, 20), "user@example.com"));
        transactionList.add(new Transaction(9L, 108, "Bonus", 1, "Quarterly bonus", 300.00, LocalDate.of(2025, 2, 15), "user@example.com"));
        transactionList.add(new Transaction(10L, 109, "Healthcare", 0, "Doctor's appointment", -120.00, LocalDate.of(2025, 2, 10), "user@example.com"));
    }

    private void loadNextPage() {
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, transactionList.size());

        if (start < end) {
            List<Transaction> nextPage = transactionList.subList(start, end);
            transactionAdapter.addMoreTransactions(nextPage);
            currentPage++;
        }

        isLoading = false;
    }
}


