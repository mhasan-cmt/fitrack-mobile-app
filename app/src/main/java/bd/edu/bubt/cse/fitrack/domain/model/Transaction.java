package bd.edu.bubt.cse.fitrack.domain.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private Long transactionId;

    private int categoryId;

    private String categoryName;

    private int transactionType;

    private String description;

    private double amount;

    private String date;

    private String userEmail;
}
