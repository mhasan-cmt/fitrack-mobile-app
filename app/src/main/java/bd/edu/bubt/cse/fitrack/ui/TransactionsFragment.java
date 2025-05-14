package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import bd.edu.bubt.cse.fitrack.ui.adapter.TransactionAdapter;

public class TransactionsFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private FloatingActionButton fabAddTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transactions, container, false);

        rvTransactions = root.findViewById(R.id.rv_transactions);
        fabAddTransaction = root.findViewById(R.id.fab_add_transaction);

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up FAB click listener
        fabAddTransaction.setOnClickListener(v -> {
            // Navigate to add transaction screen
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new AddTransactionFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Load Transactions
        loadTransactions();

        return root;
    }

    private void loadTransactions() {
        // In a real app, this would come from a repository or ViewModel
        transactionList = new ArrayList<>();
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

        transactionAdapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(transactionAdapter);
    }
}
