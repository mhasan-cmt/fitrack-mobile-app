package bd.edu.bubt.cse.fitrack.data.dto;

public class CreateCategoryRequest {
    private String categoryName;
    private Long transactionTypeId;

    private Integer userId;

    public CreateCategoryRequest(String categoryName, Long transactionTypeId, Integer userId) {
        this.categoryName = categoryName;
        this.transactionTypeId = transactionTypeId;
        this.userId = userId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
