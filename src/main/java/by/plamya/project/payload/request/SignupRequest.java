package by.plamya.project.payload.request;

import by.plamya.project.utils.annotations.PasswordMatches;
import by.plamya.project.utils.annotations.ValidEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record SignupRequest(
                @Email(message = "Здесь должен быть Email") @NotBlank(message = "Это поле обязательно") @ValidEmail String email,
                @NotEmpty(message = "Вы пропустили Имя") String lastname,
                @NotEmpty(message = "Вы пропустили Логин") String username,
                @NotEmpty(message = "Вы пропустили Пароль") @Size(min = 6) String password, String confirmPassword)
                implements PasswordMatchable {

        @Override
        public String getPassword() {
                return password != null ? password : "";
        }

        @Override
        public String getConfirmPassword() {
                return confirmPassword != null ? confirmPassword : "";
        }
}
