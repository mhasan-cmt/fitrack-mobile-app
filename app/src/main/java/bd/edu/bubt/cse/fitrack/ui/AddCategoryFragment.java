package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CreateCategoryRequest;
import bd.edu.bubt.cse.fitrack.data.dto.ProfileResponse;
import bd.edu.bubt.cse.fitrack.data.local.TokenManager;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.CategoryViewModel;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ProfileViewModel;

public class AddCategoryFragment extends Fragment {

    private TextInputEditText etCategoryName;
    private TextInputEditText etCategoryDescription;
    private RadioGroup rgCategoryType;
    private RadioButton rbExpense;
    private RadioButton rbIncome;
    private Button btnSaveCategory;
    private CategoryViewModel categoryViewModel;
    private String userEmail = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_category, container, false);

        // Initialize views
        etCategoryName = root.findViewById(R.id.et_category_name);
        rgCategoryType = root.findViewById(R.id.rg_category_type);
        rbExpense = root.findViewById(R.id.rb_expense);
        rbIncome = root.findViewById(R.id.rb_income);
        btnSaveCategory = root.findViewById(R.id.btn_save_category);

        Toolbar toolbar = root.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Add Category");
        }

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        setHasOptionsMenu(true);;

        observeViewModel();

        btnSaveCategory.setOnClickListener(v -> saveCategory());

        return root;
    }

    private void observeViewModel() {
        categoryViewModel.getCategoryState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof CategoryViewModel.CategoryState.Success) {
                Toast.makeText(getContext(), "Category added successfully!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack(); // Navigate back
            } else if (state instanceof CategoryViewModel.CategoryState.Error) {
                String msg = ((CategoryViewModel.CategoryState.Error) state).getMessage();
                Toast.makeText(getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });

        categoryViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // You can show/hide progress bar here if you have one
            if (isLoading) {
                btnSaveCategory.setEnabled(false);
            } else {
                btnSaveCategory.setEnabled(true);
            }
        });

        categoryViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCategory() {
        // Validate inputs
        String name = etCategoryName.getText().toString().trim();
        Category.TransactionTypeEnum type = rbIncome.isChecked() ? Category.TransactionTypeEnum.TYPE_INCOME : Category.TransactionTypeEnum.TYPE_EXPENSE;

        if (name.isEmpty()) {
            etCategoryName.setError("Category name is required");
            return;
        }


        Long t = type == Category.TransactionTypeEnum.TYPE_INCOME ? 2L : 1L;
        CreateCategoryRequest category = new CreateCategoryRequest(name, t, Math.toIntExact(TokenManager.getInstance(requireContext()).getUserID()));

        categoryViewModel.addCategory(category);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getParentFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
