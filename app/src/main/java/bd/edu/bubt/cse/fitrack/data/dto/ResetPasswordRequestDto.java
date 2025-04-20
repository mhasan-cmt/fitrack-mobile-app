package bd.edu.bubt.cse.fitrack.data.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordRequestDto {
    private String email;
    private String newPassword;
}
