package bd.edu.bubt.cse.fitrack.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.edu.bubt.cse.fitrack.R;
import bd.edu.bubt.cse.fitrack.domain.model.Transaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactionList;
    private List<Transaction> allTransactions;


    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = new ArrayList<>(transactionList);
        this.allTransactions = new ArrayList<>(transactionList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.tvTitle.setText(transaction.getDescription());
        holder.tvDate.setText(transaction.getDate().toString());
        holder.tvAmount.setText("$" + transaction.getAmount());


        // Change color based on amount
        if (transaction.getAmount() < 0) {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }

    // 🔍 Search filter
    public void filter(String query) {
        transactionList.clear();
        if (query == null || query.trim().isEmpty()) {
            transactionList.addAll(allTransactions);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Transaction t : allTransactions) {
                if (t.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    transactionList.add(t);
                }
            }
        }
        notifyDataSetChanged();
    }

    // 🔄 Add more transactions for pagination
    public void addMoreTransactions(List<Transaction> moreTransactions) {
        int startPos = transactionList.size();
        transactionList.addAll(moreTransactions);
        allTransactions.addAll(moreTransactions);
        notifyItemRangeInserted(startPos, moreTransactions.size());
    }

    public void clear() {
        int oldSize = transactionList.size();
        transactionList.clear();
        allTransactions.clear();
        if (oldSize > 0) {
            notifyItemRangeRemoved(0, oldSize);
        }
    }


}

