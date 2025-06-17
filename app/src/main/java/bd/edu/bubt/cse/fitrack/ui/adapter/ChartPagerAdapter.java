package bd.edu.bubt.cse.fitrack.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import bd.edu.bubt.cse.fitrack.ui.CategoryBreakdownFragment;
import bd.edu.bubt.cse.fitrack.ui.DailyExpenseFragment;
import bd.edu.bubt.cse.fitrack.ui.MonthlyChartFragment;
import bd.edu.bubt.cse.fitrack.ui.YearlyChartFragment;

public class ChartPagerAdapter extends FragmentStateAdapter {
    public ChartPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DailyExpenseFragment();
            case 1:
                return new MonthlyChartFragment();
            case 2:
                return new YearlyChartFragment();
            case 3:
                return new CategoryBreakdownFragment();
            default:
                throw new IllegalArgumentException("Invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Monthly, Category, Daily, Yearly
    }
}