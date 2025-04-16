package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.edu.bubt.cse.fitrack.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import bd.edu.bubt.cse.fitrack.ui.adapter.TransactionAdapter;

public class DashboardFragment extends Fragment {

    private RecyclerView rvTransactions;
    private bd.edu.bubt.cse.fitrack.ui.adapter.TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        rvTransactions = root.findViewById(R.id.rv_transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load Dummy Transactions
        loadDummyTransactions();

        return root;
    }

    private void loadDummyTransactions() {
        transactionList = new ArrayList<>();
transactionList.add(new Transaction(1L, 101, "Groceries", 0, "Bought groceries", -45.99, LocalDate.of(2025, 3, 15), "user@example.com"));
transactionList.add(new Transaction(2L, 102, "Income", 1, "Monthly salary", 1200.00, LocalDate.of(2025, 3, 10), "user@example.com"));
transactionList.add(new Transaction(3L, 103, "Utilities", 0, "Paid electric bill", -75.50, LocalDate.of(2025, 3, 8), "user@example.com"));
transactionList.add(new Transaction(4L, 104, "Dining", 0, "Dinner at a restaurant", -30.25, LocalDate.of(2025, 3, 5), "user@example.com"));
transactionList.add(new Transaction(5L, 105, "Freelance", 1, "Freelance project payment", 500.00, LocalDate.of(2025, 3, 2), "user@example.com"));

        transactionAdapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(transactionAdapter);
    }
}
