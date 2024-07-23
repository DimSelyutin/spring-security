package by.plamya.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class UserDTO {

    private Long id;
    @NotEmpty
    private String lastname;

    private String username;
    
    private String email;

}
