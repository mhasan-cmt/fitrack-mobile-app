package bd.edu.bubt.cse.fitrack.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.databinding.ActivityOnboardingBinding;
import bd.edu.bubt.cse.fitrack.ui.adapter.OnboardingAdapter;

public class Onboarding extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private ViewPager2 viewPager;
    private OnboardingAdapter onboardingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewPager = binding.viewPager;

        String[] titles = {
                "Welcome to Fitrack!",
                "Track Your Expenses",
                "Plan Your Savings"
        };

        String[] descriptions = {
                "Manage your finances effortlessly.",
                "Keep track of your spending.",
                "Save for your future goals."
        };

        onboardingAdapter = new OnboardingAdapter(titles, descriptions);
        viewPager.setAdapter(onboardingAdapter);

        new TabLayoutMediator(binding.tabLayout, viewPager,
                (tab, position) -> {
                }).attach();

        binding.btnGetStarted.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isOnboardingDone", true);
            editor.apply();

            Intent intent = new Intent(Onboarding.this, Register.class);
            startActivity(intent);
            finish();
        });
    }
}
