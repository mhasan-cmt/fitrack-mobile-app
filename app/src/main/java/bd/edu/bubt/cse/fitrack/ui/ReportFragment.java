package bd.edu.bubt.cse.fitrack.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.databinding.FragmentReportBinding;
import bd.edu.bubt.cse.fitrack.ui.adapter.ChartPagerAdapter;
import lombok.NonNull;

public class ReportFragment extends Fragment {
    private FragmentReportBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup ViewPager and Tabs
        ViewPager2 viewPager = binding.viewPager;
        viewPager.setAdapter(new ChartPagerAdapter(requireActivity())); // Use FragmentActivity

        TabLayout tabLayout = binding.tabLayout;
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Monthly"); break;
                        case 1: tab.setText("Categories"); break;
//                        case 2: tab.setText("Daily"); break;
                    }
                }
        ).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
