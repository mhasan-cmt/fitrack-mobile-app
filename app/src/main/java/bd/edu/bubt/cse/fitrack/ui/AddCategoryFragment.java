package bd.edu.bubt.cse.fitrack.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.local.TokenManager;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.CategoryViewModel;

public class AddCategoryFragment extends Fragment {

    private TextInputEditText etCategoryName;
    private TextInputEditText etCategoryDescription;
    private RadioGroup rgCategoryType;
    private RadioButton rbExpense;
    private RadioButton rbIncome;
    private Button btnSaveCategory;
    private CategoryViewModel categoryViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_category, container, false);

        // Initialize views
        etCategoryName = root.findViewById(R.id.et_category_name);
        rgCategoryType = root.findViewById(R.id.rg_category_type);
        rbExpense = root.findViewById(R.id.rb_expense);
        rbIncome = root.findViewById(R.id.rb_income);
        btnSaveCategory = root.findViewById(R.id.btn_save_category);

        // Set up click listener for save button
        btnSaveCategory.setOnClickListener(v -> saveCategory());

        return root;
    }

    private void saveCategory() {
        // Validate inputs
        String name = etCategoryName.getText().toString().trim();
        String description = etCategoryDescription.getText().toString().trim();
        Category.TransactionTypeEnum type = rbIncome.isChecked() ? Category.TransactionTypeEnum.TYPE_INCOME :  Category.TransactionTypeEnum.TYPE_EXPENSE;

        if (name.isEmpty()) {
            etCategoryName.setError("Category name is required");
            return;
        }

        if (description.isEmpty()) {
            etCategoryDescription.setError("Description is required");
            return;
        }

        // Create category object
        Category category = new Category();
        category.setCategoryName(name);
        // userEmail will be set by the backend based on the authenticated user

        // TODO: Save category to backend
        // For now, just show a success message and go back
        Toast.makeText(getContext(), "Category saved successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate back to categories list
        getParentFragmentManager().popBackStack();
    }
}