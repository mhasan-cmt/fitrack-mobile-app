package bd.edu.bubt.cse.fitrack.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.dto.ApiResponseDto;
import bd.edu.bubt.cse.fitrack.data.dto.CreateCategoryRequest;
import bd.edu.bubt.cse.fitrack.data.repository.CategoryRepository;
import bd.edu.bubt.cse.fitrack.domain.model.Category;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository categoryRepository;
    private final MutableLiveData<CategoriesState> categoriesState = new MutableLiveData<>();
    private final MutableLiveData<CategoryState> categoryState = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        this.categoryRepository = new CategoryRepository(application);
    }

    public LiveData<CategoriesState> getCategoriesState() {
        return categoriesState;
    }

    public LiveData<CategoryState> getCategoryState() {
        return categoryState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void getAllCategories() {
        isLoading.setValue(true);

        categoryRepository.getAllCategories(new CategoryRepository.CategoryCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                isLoading.postValue(false);
                categoriesState.postValue(new CategoriesState.Success(result));
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                categoriesState.postValue(new CategoriesState.Error(errorMsg));
            }
        });
    }

    public void addCategory(CreateCategoryRequest category) {
        // Validation
        if (category == null) {
            errorMessage.setValue("Category data is missing.");
            categoryState.setValue(new CategoryState.Error("Category data is missing."));
            return;
        }
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            errorMessage.setValue("Category name is required.");
            categoryState.setValue(new CategoryState.Error("Category name is required."));
            return;
        }
        if (category.getTransactionTypeId() == null) {
            errorMessage.setValue("Transaction type is required.");
            categoryState.setValue(new CategoryState.Error("Transaction type is required."));
            return;
        }
        if (category.getUserId() == null) {
            errorMessage.setValue("User ID is required.");
            categoryState.setValue(new CategoryState.Error("User ID is required."));
            return;
        }

        isLoading.setValue(true);

        categoryRepository.addCategory(category, new CategoryRepository.CategoryCallback<>() {
            @Override
            public void onSuccess(ApiResponseDto<String> result) {
                isLoading.postValue(false);
                categoryState.postValue(new CategoryState.Success(null));
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
                categoryState.postValue(new CategoryState.Error(errorMsg));
            }
        });
    }

    public static abstract class CategoriesState {

        public static class Loading extends CategoriesState {
            // Empty class
        }

        public static class Success extends CategoriesState {
            private final List<Category> data;

            public Success(List<Category> data) {
                this.data = data;
            }

            public List<Category> getData() {
                return data;
            }
        }

        public static class Error extends CategoriesState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }
        }
    }

    public static class CategoryState {
        public static class Loading extends CategoryState {
            // Empty class
        }

        public static class Success extends CategoryState {
            private final String data;

            public Success(String data) {
                this.data = data;
            }

            public String getData() {
                return data;
            }
        }

        public static class Error extends CategoryState {
            private final String message;

            public Error(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }
        }
    }
}
