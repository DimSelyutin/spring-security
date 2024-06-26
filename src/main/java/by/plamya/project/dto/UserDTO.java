package by.plamya.project.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class UserDTO {

    private Long id;
    @NotEmpty
    private String lastname;

    private String username;

    


}
