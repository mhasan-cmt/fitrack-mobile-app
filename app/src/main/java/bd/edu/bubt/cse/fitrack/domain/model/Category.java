package bd.edu.bubt.cse.fitrack.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Long categoryId;
    private String categoryName;
    private TransactionType transactionType;
    private boolean enabled;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionType {
        private Long transactionTypeId;
        private String transactionTypeName;
    }

    public enum TransactionTypeEnum{
        TYPE_EXPENSE, TYPE_INCOME
    }
}