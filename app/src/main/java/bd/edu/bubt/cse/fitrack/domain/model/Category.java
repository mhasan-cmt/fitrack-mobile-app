package bd.edu.bubt.cse.fitrack.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Long categoryId;
    
    private String name;
    
    private String description;
    
    private int type; // 0 for expense, 1 for income
    
    private String userEmail;
}