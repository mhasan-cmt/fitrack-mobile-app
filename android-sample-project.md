# Android Sample Project Structure

This document provides a recommended project structure for the Android application that will integrate with the Expense Tracker backend.

## Project Architecture

We recommend using the MVVM (Model-View-ViewModel) architecture pattern with Clean Architecture principles. This provides a clear separation of concerns and makes the codebase more maintainable and testable.

### Layers

1. **Presentation Layer** - Contains UI components (Activities, Fragments) and ViewModels
2. **Domain Layer** - Contains business logic and use cases
3. **Data Layer** - Contains repositories, data sources, and models

## Project Structure

### Java Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/expensetracker/
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   │   ├── ApiClient.java
│   │   │   │   │   ├── AuthInterceptor.java
│   │   │   │   │   ├── services/
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── UserService.java
│   │   │   │   │   │   ├── TransactionService.java
│   │   │   │   │   │   ├── CategoryService.java
│   │   │   │   ├── models/
│   │   │   │   │   ├── requests/
│   │   │   │   │   │   ├── SignInRequest.java
│   │   │   │   │   │   ├── SignUpRequest.java
│   │   │   │   │   │   ├── TransactionRequest.java
│   │   │   │   │   │   ├── CategoryRequest.java
│   │   │   │   │   ├── responses/
│   │   │   │   │   │   ├── ApiResponse.java
│   │   │   │   │   │   ├── JwtResponse.java
│   │   │   │   │   │   ├── UserResponse.java
│   │   │   │   │   │   ├── TransactionResponse.java
│   │   │   │   │   │   ├── CategoryResponse.java
│   │   │   │   ├── repositories/
│   │   │   │   │   ├── AuthRepository.java
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── TransactionRepository.java
│   │   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── local/
│   │   │   │   │   ├── TokenManager.java
│   │   │   │   │   ├── UserPreferences.java
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.java
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── TransactionDao.java
│   │   │   │   │   │   │   ├── CategoryDao.java
│   │   │   │   │   │   ├── entities/
│   │   │   │   │   │   │   ├── TransactionEntity.java
│   │   │   │   │   │   │   ├── CategoryEntity.java
│   │   │   ├── domain/
│   │   │   │   ├── usecases/
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── LoginUseCase.java
│   │   │   │   │   │   ├── RegisterUseCase.java
│   │   │   │   │   │   ├── VerifyRegistrationUseCase.java
│   │   │   │   │   ├── user/
│   │   │   │   │   │   ├── GetUserProfileUseCase.java
│   │   │   │   │   │   ├── UpdateUserProfileUseCase.java
│   │   │   │   │   ├── transaction/
│   │   │   │   │   │   ├── GetTransactionsUseCase.java
│   │   │   │   │   │   ├── CreateTransactionUseCase.java
│   │   │   │   │   │   ├── UpdateTransactionUseCase.java
│   │   │   │   │   │   ├── DeleteTransactionUseCase.java
│   │   │   │   │   ├── category/
│   │   │   │   │   │   ├── GetCategoriesUseCase.java
│   │   │   │   │   │   ├── CreateCategoryUseCase.java
│   │   │   │   │   │   ├── UpdateCategoryUseCase.java
│   │   │   │   │   │   ├── DeleteCategoryUseCase.java
│   │   │   │   ├── models/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Transaction.java
│   │   │   │   │   ├── Category.java
│   │   │   ├── presentation/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── login/
│   │   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   │   ├── LoginViewModel.java
│   │   │   │   │   ├── register/
│   │   │   │   │   │   ├── RegisterActivity.java
│   │   │   │   │   │   ├── RegisterViewModel.java
│   │   │   │   │   ├── verify/
│   │   │   │   │   │   ├── VerifyActivity.java
│   │   │   │   │   │   ├── VerifyViewModel.java
│   │   │   │   ├── main/
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   ├── MainViewModel.java
│   │   │   │   ├── dashboard/
│   │   │   │   │   ├── DashboardFragment.java
│   │   │   │   │   ├── DashboardViewModel.java
│   │   │   │   ├── transactions/
│   │   │   │   │   ├── list/
│   │   │   │   │   │   ├── TransactionsFragment.java
│   │   │   │   │   │   ├── TransactionsViewModel.java
│   │   │   │   │   │   ├── TransactionAdapter.java
│   │   │   │   │   ├── detail/
│   │   │   │   │   │   ├── TransactionDetailFragment.java
│   │   │   │   │   │   ├── TransactionDetailViewModel.java
│   │   │   │   │   ├── create/
│   │   │   │   │   │   ├── CreateTransactionFragment.java
│   │   │   │   │   │   ├── CreateTransactionViewModel.java
│   │   │   │   ├── categories/
│   │   │   │   │   ├── list/
│   │   │   │   │   │   ├── CategoriesFragment.java
│   │   │   │   │   │   ├── CategoriesViewModel.java
│   │   │   │   │   │   ├── CategoryAdapter.java
│   │   │   │   │   ├── detail/
│   │   │   │   │   │   ├── CategoryDetailFragment.java
│   │   │   │   │   │   ├── CategoryDetailViewModel.java
│   │   │   │   │   ├── create/
│   │   │   │   │   │   ├── CreateCategoryFragment.java
│   │   │   │   │   │   ├── CreateCategoryViewModel.java
│   │   │   │   ├── profile/
│   │   │   │   │   ├── ProfileFragment.java
│   │   │   │   │   ├── ProfileViewModel.java
│   │   │   │   ├── common/
│   │   │   │   │   ├── BaseActivity.java
│   │   │   │   │   ├── BaseFragment.java
│   │   │   │   │   ├── BaseViewModel.java
│   │   │   │   │   ├── adapters/
│   │   │   │   │   ├── utils/
│   │   │   ├── utils/
│   │   │   │   ├── Constants.java
│   │   │   │   ├── DateUtils.java
│   │   │   │   ├── CurrencyUtils.java
│   │   │   │   ├── ValidationUtils.java
│   │   │   ├── ExpenseTrackerApplication.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_login.xml
│   │   │   │   ├── activity_register.xml
│   │   │   │   ├── activity_verify.xml
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── fragment_dashboard.xml
│   │   │   │   ├── fragment_transactions.xml
│   │   │   │   ├── fragment_transaction_detail.xml
│   │   │   │   ├── fragment_create_transaction.xml
│   │   │   │   ├── fragment_categories.xml
│   │   │   │   ├── fragment_category_detail.xml
│   │   │   │   ├── fragment_create_category.xml
│   │   │   │   ├── fragment_profile.xml
│   │   │   │   ├── item_transaction.xml
│   │   │   │   ├── item_category.xml
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   ├── styles.xml
│   │   │   │   ├── dimens.xml
│   │   │   ├── drawable/
│   │   │   ├── navigation/
│   │   │   │   ├── nav_graph.xml
│   │   ├── AndroidManifest.xml
│   ├── test/
│   ├── androidTest/
├── build.gradle
```

### Kotlin Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/expensetracker/
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   │   ├── ApiClient.kt
│   │   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   │   ├── services/
│   │   │   │   │   │   ├── AuthService.kt
│   │   │   │   │   │   ├── UserService.kt
│   │   │   │   │   │   ├── TransactionService.kt
│   │   │   │   │   │   ├── CategoryService.kt
│   │   │   │   ├── models/
│   │   │   │   │   ├── requests/
│   │   │   │   │   │   ├── SignInRequest.kt
│   │   │   │   │   │   ├── SignUpRequest.kt
│   │   │   │   │   │   ├── TransactionRequest.kt
│   │   │   │   │   │   ├── CategoryRequest.kt
│   │   │   │   │   ├── responses/
│   │   │   │   │   │   ├── ApiResponse.kt
│   │   │   │   │   │   ├── JwtResponse.kt
│   │   │   │   │   │   ├── UserResponse.kt
│   │   │   │   │   │   ├── TransactionResponse.kt
│   │   │   │   │   │   ├── CategoryResponse.kt
│   │   │   │   ├── repositories/
│   │   │   │   │   ├── AuthRepository.kt
│   │   │   │   │   ├── UserRepository.kt
│   │   │   │   │   ├── TransactionRepository.kt
│   │   │   │   │   ├── CategoryRepository.kt
│   │   │   │   ├── local/
│   │   │   │   │   ├── TokenManager.kt
│   │   │   │   │   ├── UserPreferences.kt
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── TransactionDao.kt
│   │   │   │   │   │   │   ├── CategoryDao.kt
│   │   │   │   │   │   ├── entities/
│   │   │   │   │   │   │   ├── TransactionEntity.kt
│   │   │   │   │   │   │   ├── CategoryEntity.kt
│   │   │   ├── domain/
│   │   │   │   ├── usecases/
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── LoginUseCase.kt
│   │   │   │   │   │   ├── RegisterUseCase.kt
│   │   │   │   │   │   ├── VerifyRegistrationUseCase.kt
│   │   │   │   │   ├── user/
│   │   │   │   │   │   ├── GetUserProfileUseCase.kt
│   │   │   │   │   │   ├── UpdateUserProfileUseCase.kt
│   │   │   │   │   ├── transaction/
│   │   │   │   │   │   ├── GetTransactionsUseCase.kt
│   │   │   │   │   │   ├── CreateTransactionUseCase.kt
│   │   │   │   │   │   ├── UpdateTransactionUseCase.kt
│   │   │   │   │   │   ├── DeleteTransactionUseCase.kt
│   │   │   │   │   ├── category/
│   │   │   │   │   │   ├── GetCategoriesUseCase.kt
│   │   │   │   │   │   ├── CreateCategoryUseCase.kt
│   │   │   │   │   │   ├── UpdateCategoryUseCase.kt
│   │   │   │   │   │   ├── DeleteCategoryUseCase.kt
│   │   │   │   ├── models/
│   │   │   │   │   ├── User.kt
│   │   │   │   │   ├── Transaction.kt
│   │   │   │   │   ├── Category.kt
│   │   │   ├── presentation/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── login/
│   │   │   │   │   │   ├── LoginActivity.kt
│   │   │   │   │   │   ├── LoginViewModel.kt
│   │   │   │   │   ├── register/
│   │   │   │   │   │   ├── RegisterActivity.kt
│   │   │   │   │   │   ├── RegisterViewModel.kt
│   │   │   │   │   ├── verify/
│   │   │   │   │   │   ├── VerifyActivity.kt
│   │   │   │   │   │   ├── VerifyViewModel.kt
│   │   │   │   ├── main/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── MainViewModel.kt
│   │   │   │   ├── dashboard/
│   │   │   │   │   ├── DashboardFragment.kt
│   │   │   │   │   ├── DashboardViewModel.kt
│   │   │   │   ├── transactions/
│   │   │   │   │   ├── list/
│   │   │   │   │   │   ├── TransactionsFragment.kt
│   │   │   │   │   │   ├── TransactionsViewModel.kt
│   │   │   │   │   │   ├── TransactionAdapter.kt
│   │   │   │   │   ├── detail/
│   │   │   │   │   │   ├── TransactionDetailFragment.kt
│   │   │   │   │   │   ├── TransactionDetailViewModel.kt
│   │   │   │   │   ├── create/
│   │   │   │   │   │   ├── CreateTransactionFragment.kt
│   │   │   │   │   │   ├── CreateTransactionViewModel.kt
│   │   │   │   ├── categories/
│   │   │   │   │   ├── list/
│   │   │   │   │   │   ├── CategoriesFragment.kt
│   │   │   │   │   │   ├── CategoriesViewModel.kt
│   │   │   │   │   │   ├── CategoryAdapter.kt
│   │   │   │   │   ├── detail/
│   │   │   │   │   │   ├── CategoryDetailFragment.kt
│   │   │   │   │   │   ├── CategoryDetailViewModel.kt
│   │   │   │   │   ├── create/
│   │   │   │   │   │   ├── CreateCategoryFragment.kt
│   │   │   │   │   │   ├── CreateCategoryViewModel.kt
│   │   │   │   ├── profile/
│   │   │   │   │   ├── ProfileFragment.kt
│   │   │   │   │   ├── ProfileViewModel.kt
│   │   │   │   ├── common/
│   │   │   │   │   ├── BaseActivity.kt
│   │   │   │   │   ├── BaseFragment.kt
│   │   │   │   │   ├── BaseViewModel.kt
│   │   │   │   │   ├── adapters/
│   │   │   │   │   ├── extensions/
│   │   │   │   │   ├── utils/
│   │   │   ├── di/
│   │   │   │   ├── modules/
│   │   │   │   │   ├── NetworkModule.kt
│   │   │   │   │   ├── RepositoryModule.kt
│   │   │   │   │   ├── UseCaseModule.kt
│   │   │   │   │   ├── ViewModelModule.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   ├── utils/
│   │   │   │   ├── Constants.kt
│   │   │   │   ├── DateUtils.kt
│   │   │   │   ├── CurrencyUtils.kt
│   │   │   │   ├── ValidationUtils.kt
│   │   │   ├── ExpenseTrackerApplication.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_login.xml
│   │   │   │   ├── activity_register.xml
│   │   │   │   ├── activity_verify.xml
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── fragment_dashboard.xml
│   │   │   │   ├── fragment_transactions.xml
│   │   │   │   ├── fragment_transaction_detail.xml
│   │   │   │   ├── fragment_create_transaction.xml
│   │   │   │   ├── fragment_categories.xml
│   │   │   │   ├── fragment_category_detail.xml
│   │   │   │   ├── fragment_create_category.xml
│   │   │   │   ├── fragment_profile.xml
│   │   │   │   ├── item_transaction.xml
│   │   │   │   ├── item_category.xml

## Key Components Implementation

### Java Implementation

#### ApiClient.java

```java
public class ApiClient {
    private static final String BASE_URL = "http://your-backend-url/";
    private static ApiClient instance;
    private final Retrofit retrofit;

    private ApiClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
```

### Kotlin Implementation

#### ApiClient.kt

```kotlin
object ApiClient {
    private const val BASE_URL = "http://your-backend-url/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    inline fun <reified T> createService(): T {
        return retrofit.create(T::class.java)
    }
}
```

#### AuthRepository.java

```java
public class AuthRepository {
    private final AuthService authService;
    private final TokenManager tokenManager;

    public AuthRepository(AuthService authService, TokenManager tokenManager) {
        this.authService = authService;
        this.tokenManager = tokenManager;
    }

    public void login(String email, String password, Callback<Result<JwtResponse>> callback) {
        SignInRequest request = new SignInRequest(email, password);

        authService.login(request).enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JwtResponse jwtResponse = response.body();
                    tokenManager.saveToken(jwtResponse.getToken());
                    callback.onSuccess(Result.success(jwtResponse));
                } else {
                    callback.onError(new Exception("Login failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

    public void register(SignUpRequest request, Callback<Result<ApiResponse<Object>>> callback) {
        authService.register(request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(Result.success(response.body()));
                } else {
                    callback.onError(new Exception("Registration failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

    public void verifyRegistration(String code, Callback<Result<ApiResponse<Object>>> callback) {
        authService.verifyRegistration(code).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(Result.success(response.body()));
                } else {
                    callback.onError(new Exception("Verification failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

    public void resendVerificationCode(String email, Callback<Result<ApiResponse<Object>>> callback) {
        authService.resendVerificationCode(email).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(Result.success(response.body()));
                } else {
                    callback.onError(new Exception("Resend verification code failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

    public void logout() {
        tokenManager.clearToken();
    }

    // Callback interface for handling asynchronous operations
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}
```

#### AuthRepository.kt

```kotlin
class AuthRepository(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Result<JwtResponse> {
        return try {
            val response = authService.login(SignInRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let { jwtResponse ->
                    tokenManager.saveToken(jwtResponse.token)
                    Result.success(jwtResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: SignUpRequest): Result<ApiResponse<Any>> {
        return try {
            val response = authService.register(request)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    Result.success(apiResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyRegistration(code: String): Result<ApiResponse<Any>> {
        return try {
            val response = authService.verifyRegistration(code)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    Result.success(apiResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Verification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendVerificationCode(email: String): Result<ApiResponse<Any>> {
        return try {
            val response = authService.resendVerificationCode(email)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    Result.success(apiResponse)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Resend verification code failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}
```

#### LoginViewModel.java

```java
public class LoginViewModel extends ViewModel {
    private final LoginUseCase loginUseCase;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();

    public LoginViewModel(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public void login(String email, String password) {
        loginState.setValue(new LoginState.Loading());

        loginUseCase.execute(email, password, new AuthRepository.Callback<Result<JwtResponse>>() {
            @Override
            public void onSuccess(Result<JwtResponse> result) {
                if (result.isSuccess()) {
                    loginState.postValue(new LoginState.Success(result.getOrNull()));
                } else {
                    loginState.postValue(new LoginState.Error("Unknown error"));
                }
            }

            @Override
            public void onError(Exception e) {
                loginState.postValue(new LoginState.Error(e.getMessage()));
            }
        });
    }

    // State classes for login
    public static abstract class LoginState {

        public static class Loading extends LoginState {
            // Empty class
        }

        public static class Success extends LoginState {
            private final JwtResponse data;

            public Success(JwtResponse data) {
                this.data = data;
            }

            public JwtResponse getData() {
                return data;
            }
        }

        public static class Error extends LoginState {
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
```

#### LoginViewModel.kt

```kotlin
class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = loginUseCase(email, password)

            _loginState.value = when {
                result.isSuccess -> LoginState.Success(result.getOrNull()!!)
                result.isFailure -> LoginState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                else -> LoginState.Error("Unknown error")
            }
        }
    }

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val data: JwtResponse) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
```

#### LoginActivity.java

```java
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (validateInputs(email, password)) {
                viewModel.login(email, password);
            }
        });

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void observeViewModel() {
        viewModel.getLoginState().observe(this, state -> {
            if (state instanceof LoginViewModel.LoginState.Loading) {
                showLoading(true);
            } else if (state instanceof LoginViewModel.LoginState.Success) {
                showLoading(false);
                navigateToMain();
            } else if (state instanceof LoginViewModel.LoginState.Error) {
                showLoading(false);
                showError(((LoginViewModel.LoginState.Error) state).getMessage());
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            isValid = false;
        }

        return isValid;
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
```

#### LoginActivity.kt

```kotlin
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (validateInputs(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    showLoading(true)
                }
                is LoginViewModel.LoginState.Success -> {
                    showLoading(false)
                    navigateToMain()
                }
                is LoginViewModel.LoginState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            isValid = false
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
```

## Navigation

The app uses the Navigation Component for navigating between fragments. Here's a sample navigation graph:

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/dashboardFragment">

    <fragment
        android:id="@+id/dashboardFragment"
        android:name="com.example.expensetracker.presentation.dashboard.DashboardFragment"
        android:label="Dashboard"
        tools:layout="@layout/fragment_dashboard">
        <action
            android:id="@+id/action_dashboardFragment_to_transactionsFragment"
            app:destination="@id/transactionsFragment" />
        <action
            android:id="@+id/action_dashboardFragment_to_categoriesFragment"
            app:destination="@id/categoriesFragment" />
        <action
            android:id="@+id/action_dashboardFragment_to_profileFragment"
            app:destination="@id/profileFragment" />
    </fragment>

    <fragment
        android:id="@+id/transactionsFragment"
        android:name="com.example.expensetracker.presentation.transactions.list.TransactionsFragment"
        android:label="Transactions"
        tools:layout="@layout/fragment_transactions">
        <action
            android:id="@+id/action_transactionsFragment_to_transactionDetailFragment"
            app:destination="@id/transactionDetailFragment" />
        <action
            android:id="@+id/action_transactionsFragment_to_createTransactionFragment"
            app:destination="@id/createTransactionFragment" />
    </fragment>

    <fragment
        android:id="@+id/transactionDetailFragment"
        android:name="com.example.expensetracker.presentation.transactions.detail.TransactionDetailFragment"
        android:label="Transaction Detail"
        tools:layout="@layout/fragment_transaction_detail">
        <argument
            android:name="transactionId"
            app:argType="long" />
    </fragment>

    <fragment
        android:id="@+id/createTransactionFragment"
        android:name="com.example.expensetracker.presentation.transactions.create.CreateTransactionFragment"
        android:label="Create Transaction"
        tools:layout="@layout/fragment_create_transaction" />

    <fragment
        android:id="@+id/categoriesFragment"
        android:name="com.example.expensetracker.presentation.categories.list.CategoriesFragment"
        android:label="Categories"
        tools:layout="@layout/fragment_categories">
        <action
            android:id="@+id/action_categoriesFragment_to_categoryDetailFragment"
            app:destination="@id/categoryDetailFragment" />
        <action
            android:id="@+id/action_categoriesFragment_to_createCategoryFragment"
            app:destination="@id/createCategoryFragment" />
    </fragment>

    <fragment
        android:id="@+id/categoryDetailFragment"
        android:name="com.example.expensetracker.presentation.categories.detail.CategoryDetailFragment"
        android:label="Category Detail"
        tools:layout="@layout/fragment_category_detail">
        <argument
            android:name="categoryId"
            app:argType="long" />
    </fragment>

    <fragment
        android:id="@+id/createCategoryFragment"
        android:name="com.example.expensetracker.presentation.categories.create.CreateCategoryFragment"
        android:label="Create Category"
        tools:layout="@layout/fragment_create_category" />

    <fragment
        android:id="@+id/profileFragment"
        android:name="com.example.expensetracker.presentation.profile.ProfileFragment"
        android:label="Profile"
        tools:layout="@layout/fragment_profile" />
</navigation>
```

## Dependency Injection

The app uses Hilt for dependency injection. Here's a sample Hilt module:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideTransactionService(retrofit: Retrofit): TransactionService {
        return retrofit.create(TransactionService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryService(retrofit: Retrofit): CategoryService {
        return retrofit.create(CategoryService::class.java)
    }
}
```

## Conclusion

This sample project structure provides a solid foundation for building an Android application that integrates with the Expense Tracker backend. It follows modern Android development practices and architecture patterns, making it maintainable, testable, and scalable.

The key components implemented in this document should give you a good starting point for developing the application. You can extend and modify them as needed to fit your specific requirements.
