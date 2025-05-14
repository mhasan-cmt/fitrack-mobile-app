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

import java.util.ArrayList;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.domain.model.Category;
import bd.edu.bubt.cse.fitrack.ui.adapter.CategoryAdapter;

public class CategoriesFragment extends Fragment {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private FloatingActionButton fabAddCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_categories, container, false);

        rvCategories = root.findViewById(R.id.rv_categories);
        fabAddCategory = root.findViewById(R.id.fab_add_category);

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up FAB click listener
        fabAddCategory.setOnClickListener(v -> {
            // Navigate to add category screen
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new AddCategoryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Load Categories
        loadCategories();

        return root;
    }

    private void loadCategories() {
        // In a real app, this would come from a repository or ViewModel
        categoryList = new ArrayList<>();
        categoryList.add(new Category(101L, "Groceries", "Food and household items", 0, "user@example.com"));
        categoryList.add(new Category(102L, "Income", "Regular income sources", 1, "user@example.com"));
        categoryList.add(new Category(103L, "Utilities", "Bills and utilities", 0, "user@example.com"));
        categoryList.add(new Category(104L, "Dining", "Restaurants and eating out", 0, "user@example.com"));
        categoryList.add(new Category(105L, "Freelance", "Freelance income", 1, "user@example.com"));
        categoryList.add(new Category(106L, "Transportation", "Gas, public transport, etc.", 0, "user@example.com"));
        categoryList.add(new Category(107L, "Entertainment", "Movies, games, etc.", 0, "user@example.com"));
        categoryList.add(new Category(108L, "Bonus", "Bonuses and one-time income", 1, "user@example.com"));
        categoryList.add(new Category(109L, "Healthcare", "Medical expenses", 0, "user@example.com"));
        categoryList.add(new Category(110L, "Education", "Courses, books, etc.", 0, "user@example.com"));

        categoryAdapter = new CategoryAdapter(categoryList);
        rvCategories.setAdapter(categoryAdapter);
    }
}
