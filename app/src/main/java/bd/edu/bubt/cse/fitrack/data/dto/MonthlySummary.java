package bd.edu.bubt.cse.fitrack.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummary {
    private double income;
    private double expense;
    private int month;
}
