package bd.edu.bubt.cse.fitrack.data.api;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import bd.edu.bubt.cse.fitrack.data.local.TokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;
    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.context = context;
        this.tokenManager = TokenManager.getInstance(context);
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = tokenManager.getToken();
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }
}
