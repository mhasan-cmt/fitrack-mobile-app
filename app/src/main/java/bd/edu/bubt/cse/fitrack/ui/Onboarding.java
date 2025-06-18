package bd.edu.bubt.cse.fitrack.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
        int[] images = {
                R.drawable.onboarding_1,
                R.drawable.onboarding_2,
                R.drawable.onboarding_3
        };

        onboardingAdapter = new OnboardingAdapter(images);
        viewPager.setAdapter(onboardingAdapter);

        binding.btnGetStarted.setEnabled(false);
        binding.btnGetStarted.setAlpha(0.5f);

        // Enable Get Started only when last page is reached
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == images.length - 1) {
                    binding.btnGetStarted.setEnabled(true);
                    binding.btnGetStarted.setAlpha(1f);
                } else {
                    binding.btnGetStarted.setEnabled(false);
                    binding.btnGetStarted.setAlpha(0.5f);
                }
            }
        });


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001);
            }
        }
    }
}
