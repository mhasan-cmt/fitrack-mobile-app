package bd.edu.bubt.cse.fitrack.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import bd.edu.bubt.cse.fitrack.ui.CategoryBreakdownFragment;
import bd.edu.bubt.cse.fitrack.ui.DailyExpenseFragment;
import bd.edu.bubt.cse.fitrack.ui.MonthlyChartFragment;

public class ChartPagerAdapter extends FragmentStateAdapter {
    public ChartPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new MonthlyChartFragment();
            case 1: return new CategoryBreakdownFragment();
            case 2: return new DailyExpenseFragment();
            default: throw new IllegalArgumentException("Invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Monthly, Category, Daily
    }
}