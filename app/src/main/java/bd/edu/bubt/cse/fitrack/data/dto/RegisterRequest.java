package bd.edu.bubt.cse.fitrack.data.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String userName;
    private String email;
    private String password;
    private Set<String> roles;

}
