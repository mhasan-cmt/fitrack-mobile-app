package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CreateTransactionRequest;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.TransactionViewModel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class TransactionDetail extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION = "extra_transaction";

    private TextInputEditText etTitle, etAmount;
    private TextView tvDate, tvCategory;
    private Button btnUpdate, btnDelete, btnSelectDate, btnSelectCategory;

    private Transaction transaction;

    private TransactionViewModel transactionViewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Transaction Details");
        }

        // Init Views
        etTitle = findViewById(R.id.et_transaction_title);
        etAmount = findViewById(R.id.et_transaction_amount);
        tvDate = findViewById(R.id.tv_transaction_date);
        tvCategory = findViewById(R.id.tv_transaction_category);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectCategory = findViewById(R.id.btn_select_category);

        progressBar = new android.widget.ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(android.view.View.GONE);
        ((android.widget.LinearLayout) findViewById(android.R.id.content).getRootView().findViewById(R.id.toolbar).getParent()).addView(progressBar);

        // Get transaction
        transaction = (Transaction) getIntent().getSerializableExtra(EXTRA_TRANSACTION);

        if (transaction != null) {
            etTitle.setText(transaction.getDescription());
            etAmount.setText(String.valueOf(transaction.getAmount()));
            tvDate.setText("Date: " + transaction.getDate());
            tvCategory.setText("Category: " + transaction.getCategoryName());
        }

        btnSelectDate.setOnClickListener(v -> {
            // TODO: Open DatePickerDialog and set date
        });

        btnSelectCategory.setOnClickListener(v -> {
            // TODO: Open category selector dialog
        });

        btnUpdate.setOnClickListener(v -> {
            updateTransaction(transaction.getTransactionId());
        });

        btnDelete.setOnClickListener(v -> {
            deleteTransaction();
        });

        observe();
    }

    private void updateTransaction(Long transactionId) {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        transaction.setDescription(title);
        transaction.setAmount(amount);

        CreateTransactionRequest updateRequest = new CreateTransactionRequest(
                transaction.getCategoryId(),
                title,
                amount,
                transaction.getDate()
        );


        transactionViewModel.updateTransaction(transactionId, updateRequest);

    }

    private void deleteTransaction() {
        // TODO: Delete transaction from Room or backend API
        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void observe(){
        transactionViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(android.view.View.VISIBLE);
            } else {
                progressBar.setVisibility(android.view.View.GONE);
            }
        });
        transactionViewModel.getTransactionState().observe(this, state -> {
            if (state instanceof TransactionViewModel.TransactionState.Success) {
                Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else if (state instanceof TransactionViewModel.TransactionState.Error) {
                String msg = ((TransactionViewModel.TransactionState.Error) state).getMessage();
                Toast.makeText(this, "Update failed: " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

