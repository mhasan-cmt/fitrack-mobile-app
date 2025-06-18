package bd.edu.bubt.cse.fitrack.data.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitClient {

    private static final String BASE_URL = "https://fitrack-4zpt.onrender.com/mywallet/";
//    private static final String BASE_URL = "http://172.20.208.1:8080/mywallet/";
    private static final int CONNECT_TIMEOUT = 15;
    private static final int READ_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 30;
    private static final int MAX_IDLE_CONNECTIONS = 5;
    private static final long KEEP_ALIVE_DURATION = 5; // minutes

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create a connection pool for connection reuse
            ConnectionPool connectionPool = new ConnectionPool(
                    MAX_IDLE_CONNECTIONS,
                    KEEP_ALIVE_DURATION,
                    TimeUnit.MINUTES);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .connectionPool(connectionPool)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(logging)
                    .addInterceptor(new AuthInterceptor(context))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AuthApi getAuthApi(Context context) {
        return getRetrofitInstance(context).create(AuthApi.class);
    }

    public static TransactionApi getTransactionApi(Context context) {
        return getRetrofitInstance(context).create(TransactionApi.class);
    }

    public static CategoryApi getCategoryApi(Context context) {
        return getRetrofitInstance(context).create(CategoryApi.class);
    }

    public static ReportApi getReportApi(Context context) {
        return getRetrofitInstance(context).create(ReportApi.class);
    }
}
