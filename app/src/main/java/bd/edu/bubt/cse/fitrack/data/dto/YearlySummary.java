package bd.edu.bubt.cse.fitrack.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class YearlySummary {
    private Integer year;
    private Double income;
    private Double expense;
}
