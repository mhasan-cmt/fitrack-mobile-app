package bd.edu.bubt.cse.fitrack.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategorySummary {
    int categoryId;
    String categoryType;
    String categoryName;
    double total;
}
