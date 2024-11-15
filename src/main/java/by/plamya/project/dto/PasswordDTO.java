package by.plamya.project.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PasswordDTO {

    private String oldPassword;
    @NotEmpty
    private String token;
    @NotEmpty
    private String email;
    @NotEmpty
    private String newPassword;
}
