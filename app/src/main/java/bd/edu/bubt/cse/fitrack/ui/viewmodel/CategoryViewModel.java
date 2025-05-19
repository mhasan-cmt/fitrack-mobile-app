package bd.edu.bubt.cse.fitrack.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import bd.edu.bubt.cse.fitrack.data.repository.CategoryRepository;
import bd.edu.bubt.cse.fitrack.domain.model.Category;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository categoryRepository;
    private final MutableLiveData<CategoriesState> categoriesState = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    public CategoryViewModel(@NonNull Application application) {
        super(application);
        this.categoryRepository = new CategoryRepository(application);
    }

    public LiveData<CategoriesState> getCategoriesState() {
        return categoriesState;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void getAllCategories() {
        isLoading.setValue(true);

        categoryRepository.getAllCategories( new CategoryRepository.CategoryCallback<List<Category>>() {
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
}
