package bd.edu.bubt.cse.fitrack.ui.notification;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bd.edu.bubt.cse.fitrack.R;
import lombok.NonNull;

public class SpendingNotificationWorker extends Worker {

    public SpendingNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("finance_prefs", Context.MODE_PRIVATE);
        float income = prefs.getFloat("latest_income", 0);
        float expense = prefs.getFloat("latest_expense", 0);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastSent = prefs.getString("last_notification_date", "");

        if (expense > income && !today.equals(lastSent)) {
            sendNotification();
            prefs.edit().putString("last_notification_date", today).apply();
        }

        return Result.success();
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "spending_alert_channel")
                .setSmallIcon(R.drawable.ic_chart)
                .setContentTitle("Overspending Alert")
                .setContentText("You’re spending more than you earn today.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(101, builder.build());
    }
}
