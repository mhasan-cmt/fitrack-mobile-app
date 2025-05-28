package bd.edu.bubt.cse.fitrack.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import bd.edu.bubt.cse.fitrack.ui.TransactionDetail;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactionList;
    private List<Transaction> allTransactions;
    private OnTransactionClickListener listener;

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
        String description = transaction.getDescription();
        holder.tvIcon.setText(description != null && !description.isEmpty() ? description.substring(0, 1).toUpperCase() : "?");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(transaction);
            }
        });


        if (transaction.getTransactionType() < 2) {
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
        TextView tvTitle, tvDate, tvAmount, tvIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvIcon = itemView.findViewById(R.id.tv_icon);
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
        notifyItemRangeChanged(0, transactionList.size());
    }

    public void setTransactions(List<Transaction> transactions) {
        transactionList.clear();
        allTransactions.clear();

        transactionList.addAll(transactions);
        allTransactions.addAll(transactions);

        notifyDataSetChanged();
    }

    public void clear() {
        transactionList.clear();
        allTransactions.clear();
        notifyDataSetChanged();
    }

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }
}


