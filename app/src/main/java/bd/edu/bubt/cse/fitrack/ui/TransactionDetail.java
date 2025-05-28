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
import bd.edu.bubt.cse.fitrack.ui.viewmodel.CategoryViewModel;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.TransactionViewModel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionDetail extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION = "extra_transaction";

    private TextInputEditText etTitle, etAmount;
    private TextView tvDate, tvCategory;
    private Button btnUpdate, btnDelete, btnSelectDate, btnSelectCategory;

    private Transaction transaction;

    private TransactionViewModel transactionViewModel;
    private CategoryViewModel categoryViewModel;
    private ProgressBar progressBar;

    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.getAllCategories();

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
            showDatePickerDialog();
        });

        btnSelectCategory.setOnClickListener(v -> {
            showCategorySelectionDialog();
        });

        btnUpdate.setOnClickListener(v -> {
            updateTransaction(transaction.getTransactionId());
        });

        btnDelete.setOnClickListener(v -> {
            deleteTransaction(transaction.getTransactionId());
        });

        observe();
    }

    private void showCategorySelectionDialog() {
        if (categoryList == null || categoryList.isEmpty()) {
            android.widget.Toast.makeText(this, "No categories available", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        String[] categoryNames = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categoryNames[i] = categoryList.get(i).getCategoryName();
        }
        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setItems(categoryNames, (dialog, which) -> {
                    Category selected = categoryList.get(which);
                    tvCategory.setText("Category: " + selected.getCategoryName());
                    transaction.setCategoryId(Math.toIntExact(selected.getCategoryId()));
                })
                .show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                    String selectedDate = sdf.format(calendar.getTime());
                    tvDate.setText("Date: " + selectedDate);
                    transaction.setDate(selectedDate);
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).show();
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

    private void deleteTransaction(Long transactionId) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    transactionViewModel.deleteTransaction(transactionId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observe() {
        transactionViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(android.view.View.VISIBLE);
            } else {
                progressBar.setVisibility(android.view.View.GONE);
            }
        });
        transactionViewModel.getTransactionState().observe(this, state -> {
            if (state instanceof TransactionViewModel.TransactionState.Success) {
                setResult(RESULT_OK);
                finish();
            } else if (state instanceof TransactionViewModel.TransactionState.Error) {
                String msg = ((TransactionViewModel.TransactionState.Error) state).getMessage();
                Log.e("Error", "Update failed: " + msg);
            }
        });

        // Category loading observer
        categoryViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        categoryViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        categoryViewModel.getCategoriesState().observe(this, categoriesState -> {
            if (categoriesState instanceof CategoryViewModel.CategoriesState.Success) {
                CategoryViewModel.CategoriesState.Success success = (CategoryViewModel.CategoriesState.Success) categoriesState;
                categoryList = success.getData();
            } else if (categoriesState instanceof CategoryViewModel.CategoriesState.Error) {
                CategoryViewModel.CategoriesState.Error error = (CategoryViewModel.CategoriesState.Error) categoriesState;
                Toast.makeText(this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

