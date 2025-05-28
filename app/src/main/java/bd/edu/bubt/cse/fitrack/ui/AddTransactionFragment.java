package bd.edu.bubt.cse.fitrack.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CreateTransactionRequest;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import bd.edu.bubt.cse.fitrack.ui.adapter.CategoryAdapter;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.CategoryViewModel;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.TransactionViewModel;

public class AddTransactionFragment extends Fragment {
    private TextInputEditText etAmount;
    private Spinner spinnerCategory;
    private TextInputEditText etDescription;
    private Button btnSelectDate;
    private Button btnSaveTransaction;
    
    private LocalDate selectedDate = LocalDate.now();
    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    private ProgressBar progressBar;

    private CategoryViewModel categoryViewModel;
    private TransactionViewModel transactionViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        progressBar = root.findViewById(R.id.progress_bar);

        // Initialize views
        etAmount = root.findViewById(R.id.et_amount);
        spinnerCategory = root.findViewById(R.id.spinner_category);
        etDescription = root.findViewById(R.id.et_description);
        btnSelectDate = root.findViewById(R.id.btn_select_date);
        btnSaveTransaction = root.findViewById(R.id.btn_save_transaction);

        observeViewModel();

        categoryViewModel.getAllCategories();

        // Set up category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set up date button
        updateDateButtonText();
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Set up save button
        btnSaveTransaction.setOnClickListener(v -> saveTransaction());

        return root;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    updateDateButtonText();
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void updateDateButtonText() {
        btnSelectDate.setText(selectedDate.toString());
    }

    private void saveTransaction() {
        // Validate inputs
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();

        if (amountStr.isEmpty()) {
            etAmount.setError("Amount is required");
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            return;
        }

        if (selectedCategoryPosition < 0 || selectedCategoryPosition >= categoryList.size()) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Category selectedCategory = categoryList.get(selectedCategoryPosition);

        CreateTransactionRequest transaction = new CreateTransactionRequest();
        transaction.setCategoryId(selectedCategory.getCategoryId().intValue());
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setDate(selectedDate.toString());

        transactionViewModel.createTransaction(transaction);

        // Do not navigate back here — wait for observer to confirm success
    }


    private void observeViewModel() {
        // Category loading observer
        categoryViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        categoryViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        categoryViewModel.getCategoriesState().observe(getViewLifecycleOwner(), categoriesState -> {
            if (categoriesState instanceof CategoryViewModel.CategoriesState.Success) {
                CategoryViewModel.CategoriesState.Success success = (CategoryViewModel.CategoriesState.Success) categoriesState;
                categoryList = success.getData();
                categoryNames.clear();
                for (Category category : categoryList) {
                    categoryNames.add(category.getCategoryName());
                }
                ((ArrayAdapter<String>) spinnerCategory.getAdapter()).notifyDataSetChanged();
            } else if (categoriesState instanceof CategoryViewModel.CategoriesState.Error) {
                CategoryViewModel.CategoriesState.Error error = (CategoryViewModel.CategoriesState.Error) categoriesState;
                Toast.makeText(getContext(), "Failed to load categories: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Transaction loading observer
        transactionViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Transaction success/error observer
        transactionViewModel.getTransactionState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof TransactionViewModel.TransactionState.Success) {
                Toast.makeText(getContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack(); // Navigate back only after success
            } else if (state instanceof TransactionViewModel.TransactionState.Error) {
                String msg = ((TransactionViewModel.TransactionState.Error) state).getMessage();
                Toast.makeText(getContext(), "Failed to save transaction: " + msg, Toast.LENGTH_LONG).show();
            }
        });

        // Optional: Transaction error observer (in case used separately)
        transactionViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

}