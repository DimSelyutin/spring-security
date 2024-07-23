package by.plamya.project.payload.request;

import jakarta.validation.constraints.NotEmpty;


public record LoginRequest(
        @NotEmpty(message = "Вы пропустили email") String email,
        @NotEmpty(message = "Вы пропустили пароль") String password) {
}