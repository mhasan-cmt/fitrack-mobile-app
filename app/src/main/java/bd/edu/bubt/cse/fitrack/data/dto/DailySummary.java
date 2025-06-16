package bd.edu.bubt.cse.fitrack.data.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DailySummary {
    private BigDecimal income;
    private BigDecimal expense;
    private Integer day;
}
