package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.ProfileResponse;
import bd.edu.bubt.cse.fitrack.data.repository.CategoryRepository;
import bd.edu.bubt.cse.fitrack.databinding.FragmentCategoriesBinding;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import bd.edu.bubt.cse.fitrack.ui.adapter.CategoryAdapter;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.CategoryViewModel;
import bd.edu.bubt.cse.fitrack.ui.viewmodel.ProfileViewModel;

public class CategoriesFragment extends Fragment {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private FloatingActionButton fabAddCategory;
    private ProgressBar progressBar;
    private CategoryViewModel categoryViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_categories, container, false);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        rvCategories = root.findViewById(R.id.rv_categories);
        fabAddCategory = root.findViewById(R.id.fab_add_category);
        progressBar = root.findViewById(R.id.progress_bar);

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        observeViewModel();

        categoryViewModel.getAllCategories();

        // Set up FAB click listener
        fabAddCategory.setOnClickListener(v -> {
            // Navigate to add category screen
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new AddCategoryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return root;
    }

    private void observeViewModel() {
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

                categoryAdapter = new CategoryAdapter(categoryList);
                rvCategories.setAdapter(categoryAdapter);

            } else if (categoriesState instanceof CategoryViewModel.CategoriesState.Error) {
                CategoryViewModel.CategoriesState.Error error = (CategoryViewModel.CategoriesState.Error) categoriesState;
                Toast.makeText(getContext(), "Failed to load profile: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
