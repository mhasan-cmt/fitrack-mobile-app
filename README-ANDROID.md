# Android Integration for Expense Tracker Backend

This repository contains the backend for the Expense Tracker application. This README provides instructions for integrating the backend with an Android application, supporting both Java and Kotlin implementations.

## Overview

The Expense Tracker backend is a RESTful API built with Spring Boot. It provides endpoints for managing expenses, income, categories, budgets, and user accounts. The API is secured using JWT (JSON Web Token) authentication.

This guide includes code examples for both Java and Kotlin to accommodate different development preferences. You can choose the implementation that best fits your project requirements.

## Documentation

Detailed documentation for the Android integration is available in the following files:

- [Android Integration Guide](android-integration-guide.md) - Comprehensive guide for integrating the backend with an Android application
- [Android Sample Project Structure](android-sample-project.md) - Recommended project structure and implementation examples

## Quick Start

### 1. Setup Android Project

Create a new Android project with the recommended structure as described in the [Android Sample Project Structure](android-sample-project.md) document.

### 2. Add Dependencies

#### For Java Projects

Add the following dependencies to your `build.gradle` file:

```gradle
dependencies {
    // Retrofit for API calls
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // OkHttp for interceptors
    implementation 'com.squareup.okhttp3:okhttp:4.9.3'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.3'

    // Android Architecture Components
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.4.1'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.4.1'

    // View Binding
    buildFeatures {
        viewBinding true
    }
}
```

#### For Kotlin Projects

Add the following dependencies to your `build.gradle` file:

```gradle
dependencies {
    // Retrofit for API calls
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // OkHttp for interceptors
    implementation 'com.squareup.okhttp3:okhttp:4.9.3'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.3'

    // Coroutines for asynchronous programming
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1'

    // Hilt for dependency injection (optional)
    implementation 'com.google.dagger:hilt-android:2.44'
    kapt 'com.google.dagger:hilt-android-compiler:2.44'

    // View Binding
    buildFeatures {
        viewBinding true
    }
}
```

### 3. Configure API Client

Create an API client to communicate with the backend:

```java
// Java Implementation
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

```kotlin
// Kotlin Implementation
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

### 4. Implement Authentication

Create an authentication service to handle login and registration:

```java
// Java Implementation
public interface AuthService {
    @POST("mywallet/auth/signin")
    Call<JwtResponse> login(@Body SignInRequest request);

    @POST("mywallet/auth/signup")
    Call<ApiResponse<Object>> register(@Body SignUpRequest request);

    @GET("mywallet/auth/signup/verify")
    Call<ApiResponse<Object>> verifyRegistration(@Query("code") String code);

    @GET("mywallet/auth/signup/resend")
    Call<ApiResponse<Object>> resendVerificationCode(@Query("email") String email);
}
```

```kotlin
// Kotlin Implementation
interface AuthService {
    @POST("mywallet/auth/signin")
    suspend fun login(@Body request: SignInRequest): Response<JwtResponse>

    @POST("mywallet/auth/signup")
    suspend fun register(@Body request: SignUpRequest): Response<ApiResponse<Any>>

    @GET("mywallet/auth/signup/verify")
    suspend fun verifyRegistration(@Query("code") code: String): Response<ApiResponse<Any>>

    @GET("mywallet/auth/signup/resend")
    suspend fun resendVerificationCode(@Query("email") email: String): Response<ApiResponse<Any>>
}
```

### 5. Implement Token Management

Create a token manager to store and retrieve the JWT token:

```java
// Java Implementation
public class TokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static TokenManager instance;
    private Context context;

    private TokenManager() {
        // Private constructor to enforce singleton pattern
    }

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    public void saveToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}
```

```kotlin
// Kotlin Implementation
object TokenManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_TOKEN = "jwt_token"

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TOKEN).apply()
    }
}
```

### 6. Implement API Services

Create services for each API endpoint:

```java
// Java Implementation
public interface UserService {
    @GET("mywallet/user/profile")
    Call<ApiResponse<UserResponse>> getUserProfile();

    @PUT("mywallet/user/profile")
    Call<ApiResponse<UserResponse>> updateUserProfile(@Body UpdateUserRequest request);
}

public interface TransactionService {
    @GET("mywallet/transaction")
    Call<ApiResponse<PageResponse<TransactionResponse>>> getTransactions(
            @Query("page") int page, 
            @Query("size") int size);

    @POST("mywallet/transaction")
    Call<ApiResponse<TransactionResponse>> createTransaction(@Body TransactionRequest request);

    @GET("mywallet/transaction/{id}")
    Call<ApiResponse<TransactionResponse>> getTransaction(@Path("id") long id);

    @PUT("mywallet/transaction/{id}")
    Call<ApiResponse<TransactionResponse>> updateTransaction(
            @Path("id") long id, 
            @Body TransactionRequest request);

    @DELETE("mywallet/transaction/{id}")
    Call<ApiResponse<Object>> deleteTransaction(@Path("id") long id);
}
```

```kotlin
// Kotlin Implementation
interface UserService {
    @GET("mywallet/user/profile")
    suspend fun getUserProfile(): Response<ApiResponse<UserResponse>>

    @PUT("mywallet/user/profile")
    suspend fun updateUserProfile(@Body request: UpdateUserRequest): Response<ApiResponse<UserResponse>>
}

interface TransactionService {
    @GET("mywallet/transaction")
    suspend fun getTransactions(@Query("page") page: Int, @Query("size") size: Int): Response<ApiResponse<PageResponse<TransactionResponse>>>

    @POST("mywallet/transaction")
    suspend fun createTransaction(@Body request: TransactionRequest): Response<ApiResponse<TransactionResponse>>

    @GET("mywallet/transaction/{id}")
    suspend fun getTransaction(@Path("id") id: Long): Response<ApiResponse<TransactionResponse>>

    @PUT("mywallet/transaction/{id}")
    suspend fun updateTransaction(@Path("id") id: Long, @Body request: TransactionRequest): Response<ApiResponse<TransactionResponse>>

    @DELETE("mywallet/transaction/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long): Response<ApiResponse<Any>>
}
```

### 7. Implement Repositories

Create repositories to handle data operations:

```java
// Java Implementation
public class UserRepository {
    private final UserService userService;

    public UserRepository(UserService userService) {
        this.userService = userService;
    }

    public void getUserProfile(Callback<Result<UserResponse>> callback) {
        userService.getUserProfile().enqueue(new retrofit2.Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body().getResponse();
                    if (userResponse != null) {
                        callback.onSuccess(Result.success(userResponse));
                    } else {
                        callback.onError(new Exception("Empty response body"));
                    }
                } else {
                    callback.onError(new Exception("Failed to get user profile: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

    public void updateUserProfile(UpdateUserRequest request, Callback<Result<UserResponse>> callback) {
        userService.updateUserProfile(request).enqueue(new retrofit2.Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body().getResponse();
                    if (userResponse != null) {
                        callback.onSuccess(Result.success(userResponse));
                    } else {
                        callback.onError(new Exception("Empty response body"));
                    }
                } else {
                    callback.onError(new Exception("Failed to update user profile: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

    // Callback interface for handling asynchronous operations
    public interface Callback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}
```

```kotlin
// Kotlin Implementation
class UserRepository(private val userService: UserService) {
    suspend fun getUserProfile(): Result<UserResponse> {
        return try {
            val response = userService.getUserProfile()
            if (response.isSuccessful) {
                response.body()?.response?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to get user profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(request: UpdateUserRequest): Result<UserResponse> {
        return try {
            val response = userService.updateUserProfile(request)
            if (response.isSuccessful) {
                response.body()?.response?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to update user profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 8. Implement ViewModels

Create ViewModels to handle UI logic:

```java
// Java Implementation
public class UserViewModel extends ViewModel {
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final MutableLiveData<UserState> userState = new MutableLiveData<>();

    public UserViewModel(GetUserProfileUseCase getUserProfileUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
    }

    public LiveData<UserState> getUserState() {
        return userState;
    }

    public void getUserProfile() {
        userState.setValue(new UserState.Loading());

        getUserProfileUseCase.execute(new UserRepository.Callback<Result<UserResponse>>() {
            @Override
            public void onSuccess(Result<UserResponse> result) {
                if (result.isSuccess()) {
                    userState.postValue(new UserState.Success(result.getOrNull()));
                } else {
                    userState.postValue(new UserState.Error("Unknown error"));
                }
            }

            @Override
            public void onError(Exception e) {
                userState.postValue(new UserState.Error(e.getMessage()));
            }
        });
    }

    // State classes for user profile
    public static abstract class UserState {

        public static class Loading extends UserState {
            // Empty class
        }

        public static class Success extends UserState {
            private final UserResponse data;

            public Success(UserResponse data) {
                this.data = data;
            }

            public UserResponse getData() {
                return data;
            }
        }

        public static class Error extends UserState {
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

```kotlin
// Kotlin Implementation
class UserViewModel(private val getUserProfileUseCase: GetUserProfileUseCase) : ViewModel() {
    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> = _userState

    fun getUserProfile() {
        _userState.value = UserState.Loading

        viewModelScope.launch {
            val result = getUserProfileUseCase()

            _userState.value = when {
                result.isSuccess -> UserState.Success(result.getOrNull()!!)
                result.isFailure -> UserState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                else -> UserState.Error("Unknown error")
            }
        }
    }

    sealed class UserState {
        object Loading : UserState()
        data class Success(val data: UserResponse) : UserState()
        data class Error(val message: String) : UserState()
    }
}
```

### 9. Implement UI

Create Activities and Fragments to display data:

```java
// Java Implementation
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        setupViews();
        observeViewModel();

        viewModel.getUserProfile();
    }

    private void setupViews() {
        binding.btnUpdateProfile.setOnClickListener(v -> {
            // Navigate to update profile screen
        });
    }

    private void observeViewModel() {
        viewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UserViewModel.UserState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof UserViewModel.UserState.Success) {
                binding.progressBar.setVisibility(View.GONE);
                updateUI(((UserViewModel.UserState.Success) state).getData());
            } else if (state instanceof UserViewModel.UserState.Error) {
                binding.progressBar.setVisibility(View.GONE);
                showError(((UserViewModel.UserState.Error) state).getMessage());
            }
        });
    }

    private void updateUI(UserResponse user) {
        binding.tvUsername.setText(user.getUsername());
        binding.tvEmail.setText(user.getEmail());
        // Update other UI elements
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
```

```kotlin
// Kotlin Implementation
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()

        viewModel.getUserProfile()
    }

    private fun setupViews() {
        binding.btnUpdateProfile.setOnClickListener {
            // Navigate to update profile screen
        }
    }

    private fun observeViewModel() {
        viewModel.userState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UserViewModel.UserState.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is UserViewModel.UserState.Success -> {
                    binding.progressBar.isVisible = false
                    updateUI(state.data)
                }
                is UserViewModel.UserState.Error -> {
                    binding.progressBar.isVisible = false
                    showError(state.message)
                }
            }
        }
    }

    private fun updateUI(user: UserResponse) {
        binding.tvUsername.text = user.username
        binding.tvEmail.text = user.email
        // Update other UI elements
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
```

## Next Steps

For more detailed information on integrating the backend with an Android application, refer to the [Android Integration Guide](android-integration-guide.md) and [Android Sample Project Structure](android-sample-project.md) documents.

## Support

If you encounter any issues or have questions about integrating the backend with an Android application, please open an issue in this repository.
