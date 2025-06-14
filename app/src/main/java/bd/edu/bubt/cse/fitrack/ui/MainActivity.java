package bd.edu.bubt.cse.fitrack.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.google.android.material.navigation.NavigationView;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.local.TokenManager;
import bd.edu.bubt.cse.fitrack.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private TextView tvUserEmail;

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

        // Check if user is authenticated using TokenManager
        TokenManager tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.hasValidToken()) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return;
        }

        // Continue loading main UI
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        findViews();

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }

        // Get username from TokenManager
        tokenManager = TokenManager.getInstance(this);
        String username = tokenManager.getUsername();

        tvUserEmail.setText(username != null ? username : "User");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void findViews() {
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tvUserEmail = headerView.findViewById(R.id.user_email);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        if (item.getItemId() == R.id.nav_dashboard) {
            fragment = new DashboardFragment();
        } else if (item.getItemId() == R.id.nav_transactions) {
            fragment = new TransactionsFragment();
        } else if (item.getItemId() == R.id.nav_categories) {
            fragment = new CategoriesFragment();
        } else if (item.getItemId() == R.id.nav_profile) {
            fragment = new ProfileFragment();
        } else if (item.getItemId() == R.id.nav_budget) {
            // TODO: Implement BudgetFragment
            Toast.makeText(this, "Budget feature coming soon", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.nav_reports) {
            fragment = new ReportFragment();
        } else if (item.getItemId() == R.id.nav_settings) {
            // TODO: Implement SettingsFragment
            Toast.makeText(this, "Settings feature coming soon", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.nav_logout) {
            logoutUser();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        // Use TokenManager to securely clear all authentication data
        TokenManager tokenManager = TokenManager.getInstance(this);
        tokenManager.clearAll();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

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
}
