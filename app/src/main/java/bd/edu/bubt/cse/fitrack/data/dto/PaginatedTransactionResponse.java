package bd.edu.bubt.cse.fitrack.data.dto;

import java.util.List;
import java.util.Map;

import bd.edu.bubt.cse.fitrack.domain.model.Transaction;

public class PaginatedTransactionResponse {
    private Map<String, List<Transaction>> groupedTransactions;
    private int totalPages;

    public PaginatedTransactionResponse(Map<String, List<Transaction>> groupedTransactions, int totalPages) {
        this.groupedTransactions = groupedTransactions;
        this.totalPages = totalPages;
    }

    public Map<String, List<Transaction>> getGroupedTransactions() {
        return groupedTransactions;
    }

    public int getTotalPages() {
        return totalPages;
    }
}

