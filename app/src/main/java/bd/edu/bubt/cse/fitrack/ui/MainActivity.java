package bd.edu.bubt.cse.fitrack.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.google.android.material.navigation.NavigationView;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.local.TokenManager;
import bd.edu.bubt.cse.fitrack.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TextView tvUserEmail; // Optional, if still needed
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isOnboardingDone = sharedPreferences.getBoolean("isOnboardingDone", false);

        if (!isOnboardingDone) {
            startActivity(new Intent(MainActivity.this, Onboarding.class));
            finish();
            return;
        }

        tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.hasValidToken()) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return;
        }

        // Inflate layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("FiTrack");

        // Bottom Navigation setup
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int itemId = item.getItemId();
            if (itemId == bd.edu.bubt.cse.fitrack.R.id.nav_transactions) {
                fragment = new TransactionsFragment();
            } else if (itemId == bd.edu.bubt.cse.fitrack.R.id.nav_categories) {
                fragment = new CategoriesFragment();
            } else if (itemId == bd.edu.bubt.cse.fitrack.R.id.nav_reports) {
                fragment = new ReportFragment();
            } else {
                fragment = new DashboardFragment();
            }
            loadFragment(fragment);
            return true;
        });

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        }

        createNotificationChannel();
    }

    // Load fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    // Toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            loadFragment(new ProfileFragment());
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        tokenManager.clearAll();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }

    // Session expiration receiver
    private final BroadcastReceiver sessionExpiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Session Expired")
                    .setMessage("Please log in again.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        Intent loginIntent = new Intent(MainActivity.this, Login.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        finish();
                    })
                    .show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(sessionExpiredReceiver,
                new IntentFilter("SESSION_EXPIRED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sessionExpiredReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications may not work without permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "spending_alert_channel",
                    "Spending Alert",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alerts you when you overspend");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}

