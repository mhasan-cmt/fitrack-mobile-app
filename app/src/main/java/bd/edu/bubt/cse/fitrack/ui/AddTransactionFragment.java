package bd.edu.bubt.cse.fitrack.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;

public class AddTransactionFragment extends Fragment {

    private RadioGroup rgTransactionType;
    private RadioButton rbExpense;
    private RadioButton rbIncome;
    private TextInputEditText etAmount;
    private Spinner spinnerCategory;
    private TextInputEditText etDescription;
    private Button btnSelectDate;
    private Button btnSaveTransaction;
    
    private LocalDate selectedDate = LocalDate.now();
    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        // Initialize views
        rgTransactionType = root.findViewById(R.id.rg_transaction_type);
        rbExpense = root.findViewById(R.id.rb_expense);
        rbIncome = root.findViewById(R.id.rb_income);
        etAmount = root.findViewById(R.id.et_amount);
        spinnerCategory = root.findViewById(R.id.spinner_category);
        etDescription = root.findViewById(R.id.et_description);
        btnSelectDate = root.findViewById(R.id.btn_select_date);
        btnSaveTransaction = root.findViewById(R.id.btn_save_transaction);

        // Load categories
        loadCategories();

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

    private void loadCategories() {
        // In a real app, this would come from a repository or ViewModel
        categoryList.add(new Category(101L, "Groceries", "Food and household items", 0, 1L));
        categoryList.add(new Category(102L, "Income", "Regular income sources", 1, 1L));
        categoryList.add(new Category(103L, "Utilities", "Bills and utilities", 0, 1L));
        categoryList.add(new Category(104L, "Dining", "Restaurants and eating out", 0, 1L));
        categoryList.add(new Category(105L, "Freelance", "Freelance income", 1, 1L));
        categoryList.add(new Category(106L, "Transportation", "Gas, public transport, etc.", 0, 1L));
        categoryList.add(new Category(107L, "Entertainment", "Movies, games, etc.", 0, 1L));
        categoryList.add(new Category(108L, "Bonus", "Bonuses and one-time income", 1, 1L));
        categoryList.add(new Category(109L, "Healthcare", "Medical expenses", 0, 1L));
        categoryList.add(new Category(110L, "Education", "Courses, books, etc.", 0, 1L));

        // Extract category names for spinner
        for (Category category : categoryList) {
            categoryNames.add(category.getName());
        }
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
        int type = rbIncome.isChecked() ? 1 : 0; // 0 for expense, 1 for income
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
        // If it's an expense, make the amount negative
        if (type == 0) {
            amount = -Math.abs(amount);
        } else {
            amount = Math.abs(amount);
        }

        Category selectedCategory = categoryList.get(selectedCategoryPosition);

        // Create transaction object
        Transaction transaction = new Transaction();
        transaction.setCategoryId(selectedCategory.getCategoryId().intValue());
        transaction.setCategoryName(selectedCategory.getName());
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setDate(selectedDate);
        // userEmail will be set by the backend based on the authenticated user

        // TODO: Save transaction to backend
        // For now, just show a success message and go back
        Toast.makeText(getContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate back to transactions list
        getParentFragmentManager().popBackStack();
    }
}