package bd.edu.bubt.cse.fitrack.ui;

import android.content.Intent;
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

import com.google.android.material.navigation.NavigationView;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private TextView tvUserEmail;
    private TextView tvBalance;
    private TextView tvExpense;

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

        // Securely retrieve JWT token
        String token = null;
        token = sharedPreferences.getString("jwt_token", null);

        if (token == null) {
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

        String username = null;
        username = sharedPreferences.getString("loggedInUsername", null);

        tvUserEmail.setText(username!=null ? username : "User");

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
        tvBalance = binding.tvBalance;
        tvExpense = binding.tvExpenses;

        // Placeholder balance and expense values
        tvBalance.setText("$1200.00");
        tvExpense.setText("$151.74");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        if (item.getItemId() == R.id.nav_dashboard) {
            fragment = new DashboardFragment();
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
        SharedPreferences.Editor userEditor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        userEditor.remove("loggedInUsername");
        userEditor.apply();

        userEditor.remove("jwt_token");
        userEditor.apply();

        // Verify jwt_token is removed
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if (sharedPreferences.getString("jwt_token", null) == null) {
            Toast.makeText(this, "JWT token removed successfully", Toast.LENGTH_SHORT).show();
        }

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
}

