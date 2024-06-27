package by.plamya.project.payload.request;

import by.plamya.project.utils.annotations.PasswordMatches;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record ChangePasswordRequest(

        @NotEmpty(message = "Введите старый пароль") String oldPassword,
        @NotEmpty(message = "Введите новый пароль") @Size(min = 6) String password,
        String confirmPassword) implements PasswordMatchable {

    @Override
    public String getPassword() {
        return password != null ? password : "";
    }

    @Override
    public String getConfirmPassword() {
        return confirmPassword != null ? confirmPassword : "";
    }
    
}
