package bd.edu.bubt.cse.fitrack.ui.notification;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
        Context context = getApplicationContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "spending_alert_channel")
                .setSmallIcon(R.drawable.ic_chart)
                .setContentTitle("Overspending Alert")
                .setContentText("You’re spending more than you earn today.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(101, builder.build());
    }
}
