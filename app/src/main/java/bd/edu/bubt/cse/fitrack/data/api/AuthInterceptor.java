package bd.edu.bubt.cse.fitrack.data.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.logging.Handler;

import bd.edu.bubt.cse.fitrack.data.local.TokenManager;
import bd.edu.bubt.cse.fitrack.ui.Login;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;
    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext(); // Use ApplicationContext to avoid memory leaks
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
        Response response = chain.proceed(newRequest);

        if (response.code() == 401) {
            // Unauthorized - clear token and logout
            handleUnauthorized();
        }

        return response;
    }

    private void handleUnauthorized() {
        // Clear stored token
        tokenManager.clearAll();

        Intent intent = new Intent("SESSION_EXPIRED");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

