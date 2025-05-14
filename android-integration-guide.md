# Android Integration Guide for Expense Tracker Backend

This guide provides instructions for integrating the Expense Tracker backend with an Android application.

## Table of Contents
1. [API Overview](#api-overview)
2. [Authentication](#authentication)
3. [API Endpoints](#api-endpoints)
4. [Data Models](#data-models)
5. [Sample Code](#sample-code)
6. [Setup Instructions](#setup-instructions)

## API Overview

The Expense Tracker backend provides a RESTful API for managing expenses, income, categories, budgets, and user accounts. The API is secured using JWT (JSON Web Token) authentication.

## Authentication

### Registration Flow

1. Register a new user by sending a POST request to `/mywallet/auth/signup`
2. The server will send a verification code to the user's email
3. Verify the registration by sending a GET request to `/mywallet/auth/signup/verify?code={verificationCode}`
4. If the verification code is lost, request a new one by sending a GET request to `/mywallet/auth/signup/resend?email={email}`

### Login Flow

1. Login by sending a POST request to `/mywallet/auth/signin`
2. The server will return a JWT token
3. Include this token in the `Authorization` header of all subsequent requests as `Bearer {token}`

## API Endpoints

### Authentication Endpoints

| Endpoint | Method | Description | Request Body | Response |
|----------|--------|-------------|--------------|----------|
| `/mywallet/auth/signup` | POST | Register a new user | [SignUpRequestDto](#signuprequestdto) | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/auth/signup/verify` | GET | Verify registration | Query param: `code` | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/auth/signup/resend` | GET | Resend verification code | Query param: `email` | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/auth/signin` | POST | Login | [SignInRequestDto](#signinrequestdto) | [JwtResponseDto](#jwtresponsedto) |

### User Endpoints

| Endpoint | Method | Description | Request Body | Response |
|----------|--------|-------------|--------------|----------|
| `/mywallet/user/profile` | GET | Get user profile | - | [ApiResponseDto](#apiresponsedto) with [UserResponseDto](#userresponsedto) |
| `/mywallet/user/profile` | PUT | Update user profile | [UpdateUserRequestDto](#updateuserrequestdto) | [ApiResponseDto](#apiresponsedto) with [UserResponseDto](#userresponsedto) |

### Transaction Endpoints

| Endpoint | Method | Description | Request Body | Response |
|----------|--------|-------------|--------------|----------|
| `/mywallet/transaction` | POST | Create a new transaction | [TransactionRequestDto](#transactionrequestdto) | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/transaction` | GET | Get all transactions | Query params: `page`, `size` | [ApiResponseDto](#apiresponsedto) with [PageResponseDto](#pageresponsedto) |
| `/mywallet/transaction/{id}` | GET | Get transaction by ID | - | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/transaction/{id}` | PUT | Update transaction | [TransactionRequestDto](#transactionrequestdto) | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/transaction/{id}` | DELETE | Delete transaction | - | [ApiResponseDto](#apiresponsedto) |

### Category Endpoints

| Endpoint | Method | Description | Request Body | Response |
|----------|--------|-------------|--------------|----------|
| `/mywallet/category` | POST | Create a new category | [CategoryRequestDto](#categoryrequestdto) | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/category` | GET | Get all categories | - | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/category/{id}` | GET | Get category by ID | - | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/category/{id}` | PUT | Update category | [CategoryRequestDto](#categoryrequestdto) | [ApiResponseDto](#apiresponsedto) |
| `/mywallet/category/{id}` | DELETE | Delete category | - | [ApiResponseDto](#apiresponsedto) |

## Data Models

### SignUpRequestDto

```java
public record SignUpRequestDto(
    @NotBlank String userName,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 8, max = 20) String password,
    String phone,
    String gender,
    String firstName,
    String lastName,
    String dateOfBirth,
    String address,
    Set<String> roles
) {}
```

### SignInRequestDto

```java
public record SignInRequestDto(
    @NotBlank String email,
    @NotBlank String password
) {}
```

### JwtResponseDto

```java
@Data
@Builder
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}
```

### ApiResponseDto

```java
@Data
@AllArgsConstructor
public class ApiResponseDto<T> {
    private ApiResponseStatus status;
    private HttpStatus httpStatus;
    private T response;
}
```

## Sample Code

### Setting Up Retrofit

#### Java Implementation

```java
// ApiClient.java
public class ApiClient {
    private static final String BASE_URL = "http://your-backend-url/";
    private static ApiClient instance;
    private final Retrofit retrofit;

    private ApiClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
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

// AuthInterceptor.java
public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // Get token from SharedPreferences or other storage
        String token = TokenManager.getInstance().getToken();

        // Add token to header if available
        Request newRequest;
        if (token != null) {
            newRequest = request.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        } else {
            newRequest = request;
        }

        return chain.proceed(newRequest);
    }
}

// TokenManager.java
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

#### Kotlin Implementation

```kotlin
// ApiClient.kt
object ApiClient {
    private const val BASE_URL = "http://your-backend-url/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
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

// AuthInterceptor.kt
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Get token from SharedPreferences or other storage
        val token = TokenManager.getToken()

        // Add token to header if available
        val newRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}

// TokenManager.kt
object TokenManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_TOKEN = "jwt_token"

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TOKEN).apply()
    }
}
```

### Authentication Service

#### Java Implementation

```java
// AuthService.java
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

// SignInRequest.java
public class SignInRequest {
    private String email;
    private String password;

    public SignInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

// SignUpRequest.java
public class SignUpRequest {
    private String userName;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String address;
    private Set<String> roles;

    public SignUpRequest(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}

// JwtResponse.java
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

// ApiResponse.java
public class ApiResponse<T> {
    private String status;
    private String httpStatus;
    private T response;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
```

#### Kotlin Implementation

```kotlin
// AuthService.kt
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

// Data Classes
data class SignInRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val userName: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val gender: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val address: String? = null,
    val roles: Set<String>? = null
)

data class JwtResponse(
    val token: String,
    val type: String,
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
)

data class ApiResponse<T>(
    val status: String,
    val httpStatus: String,
    val response: T
)
```

### Using the Authentication Service

#### Java Implementation

```java
// LoginViewModel.java
public class LoginViewModel extends ViewModel {
    private final AuthService authService;
    private final MutableLiveData<Result<JwtResponse>> loginResult = new MutableLiveData<>();

    public LoginViewModel() {
        authService = ApiClient.getInstance().createService(AuthService.class);
    }

    public LiveData<Result<JwtResponse>> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        SignInRequest request = new SignInRequest(email, password);

        authService.login(request).enqueue(new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JwtResponse jwtResponse = response.body();
                    // Save token
                    TokenManager.getInstance().saveToken(jwtResponse.getToken());
                    loginResult.setValue(Result.success(jwtResponse));
                } else {
                    loginResult.setValue(Result.failure(new Exception("Login failed: " + response.message())));
                }
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                loginResult.setValue(Result.failure(new Exception(t)));
            }
        });
    }
}
```

#### Kotlin Implementation

```kotlin
// LoginViewModel.kt
class LoginViewModel : ViewModel() {
    private val authService = ApiClient.createService<AuthService>()

    private val _loginResult = MutableLiveData<Result<JwtResponse>>()
    val loginResult: LiveData<Result<JwtResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authService.login(SignInRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { jwtResponse ->
                        // Save token
                        TokenManager.saveToken(getApplication(), jwtResponse.token)
                        _loginResult.value = Result.success(jwtResponse)
                    }
                } else {
                    _loginResult.value = Result.failure(Exception("Login failed: ${response.message()}"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }
}
```

## Setup Instructions

### For Java Projects

1. Configure your Android app to communicate with the backend:
   - Add internet permission to your AndroidManifest.xml:
     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```
   - Update the BASE_URL in ApiClient.java to point to your backend server
   - Initialize TokenManager in your Application class:
     ```java
     public class MyApplication extends Application {
         @Override
         public void onCreate() {
             super.onCreate();
             TokenManager.getInstance().init(this);
         }
     }
     ```

2. Add the required dependencies to your build.gradle file:
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
   }
   ```

3. Implement the authentication flow:
   - Create a login screen that collects email and password
   - Create a registration screen that collects the required user information
   - Create a verification screen for entering the verification code
   - Use the provided ViewModels to interact with the backend

4. Implement token management:
   - Store the JWT token securely using the TokenManager
   - Add the token to all API requests using the AuthInterceptor
   - Handle token expiration by implementing a refresh mechanism or redirecting to the login screen

5. Implement error handling:
   - Handle network errors using Retrofit's Callback interface
   - Handle authentication errors
   - Display appropriate error messages to the user

### For Kotlin Projects

1. Configure your Android app to communicate with the backend:
   - Add internet permission to your AndroidManifest.xml:
     ```xml
     <uses-permission android:name="android.permission.INTERNET" />
     ```
   - Update the BASE_URL in ApiClient.kt to point to your backend server

2. Add the required dependencies to your build.gradle file:
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
   }
   ```

3. Implement the authentication flow:
   - Create a login screen that collects email and password
   - Create a registration screen that collects the required user information
   - Create a verification screen for entering the verification code
   - Use the provided ViewModels to interact with the backend

4. Implement token management:
   - Store the JWT token securely using the TokenManager
   - Add the token to all API requests using the AuthInterceptor
   - Handle token expiration by implementing a refresh mechanism or redirecting to the login screen

5. Implement error handling:
   - Handle network errors using try-catch blocks with coroutines
   - Handle authentication errors
   - Display appropriate error messages to the user
