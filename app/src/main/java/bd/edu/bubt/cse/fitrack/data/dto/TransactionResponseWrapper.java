package bd.edu.bubt.cse.fitrack.data.dto;

import java.util.List;
import java.util.Map;

import bd.edu.bubt.cse.fitrack.domain.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionResponseWrapper {
    private Map<String, List<Transaction>> data;
    private Integer totalNoOfPages;
    private Integer totalNoOfRecords;
}