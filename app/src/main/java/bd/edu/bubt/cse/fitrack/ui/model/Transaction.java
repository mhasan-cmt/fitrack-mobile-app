package bd.edu.bubt.cse.fitrack.ui.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Transaction {
    private String title;
    private String date;
    private double amount;
}
