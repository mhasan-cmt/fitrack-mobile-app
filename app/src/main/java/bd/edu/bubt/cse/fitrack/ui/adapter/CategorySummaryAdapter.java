package bd.edu.bubt.cse.fitrack.ui.adapter;

import static bd.edu.bubt.cse.fitrack.domain.model.Category.TransactionTypeEnum.TYPE_INCOME;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.data.dto.CategorySummary;
import lombok.NonNull;

public class CategorySummaryAdapter extends RecyclerView.Adapter<CategorySummaryAdapter.CategoryViewHolder> {

    private List<CategorySummary> categoryList;

    public CategorySummaryAdapter(List<CategorySummary> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card, parent, false);
        return new CategoryViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategorySummary summary = categoryList.get(position);
        holder.tvIcon.setText(summary.getCategoryName().substring(0, 1).toUpperCase());
        holder.tvName.setText(summary.getCategoryName());
        holder.tvAmount.setText(String.format("৳%.2f", summary.getTotal()));
        if (summary.getCategoryType().equals(TYPE_INCOME.name())) {
            holder.ivIncomeExpense.setImageResource(R.drawable.ic_income);
        } else {
            holder.ivIncomeExpense.setImageResource(R.drawable.ic_expense);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategoryList(List<CategorySummary> newList) {
        this.categoryList = newList;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvAmount;
        ImageView ivIncomeExpense;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
            tvAmount = itemView.findViewById(R.id.tv_category_amount);
            ivIncomeExpense = itemView.findViewById(R.id.iv_income_expense);
        }
    }
}
