package bd.edu.bubt.cse.fitrack.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.domain.model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categoryList;

    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryDescription.setText(category.getDescription());
        
        // Set type text based on category type
        if (category.getType() == 0) {
            holder.tvCategoryType.setText("Expense");
            holder.tvCategoryType.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvCategoryType.setText("Income");
            holder.tvCategoryType.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvCategoryType, tvCategoryDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryType = itemView.findViewById(R.id.tv_category_type);
            tvCategoryDescription = itemView.findViewById(R.id.tv_category_description);
        }
    }
}