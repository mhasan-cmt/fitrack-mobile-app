package bd.edu.bubt.cse.fitrack.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import bd.edu.bubt.cse.fitrack.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final String[] titles;
    private final String[] descriptions;

    public OnboardingAdapter(String[] titles, String[] descriptions) {
        this.titles = titles;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.tvOnboardingTitle.setText(titles[position]);
        holder.tvOnboardingDescription.setText(descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        TextView tvOnboardingTitle;
        TextView tvOnboardingDescription;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOnboardingTitle = itemView.findViewById(R.id.tvOnboardingTitle);
            tvOnboardingDescription = itemView.findViewById(R.id.tvOnboardingDescription);
        }
    }
}

