package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.edu.bubt.cse.fitrack.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.edu.bubt.cse.fitrack.ui.adapter.TransactionAdapter;
import bd.edu.bubt.cse.fitrack.ui.model.Transaction;

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
        transactionList.add(new Transaction("Groceries", "2025-03-15", -45.99));
        transactionList.add(new Transaction("Salary", "2025-03-10", 1200.00));
        transactionList.add(new Transaction("Electric Bill", "2025-03-08", -75.50));
        transactionList.add(new Transaction("Dining Out", "2025-03-05", -30.25));
        transactionList.add(new Transaction("Freelance Work", "2025-03-02", 500.00));

        transactionAdapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(transactionAdapter);
    }
}
