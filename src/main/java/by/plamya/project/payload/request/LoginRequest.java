package by.plamya.project.payload.request;

import jakarta.validation.constraints.NotEmpty;


public record LoginRequest(
        @NotEmpty(message = "Вы пропустили логин") String username,
        @NotEmpty(message = "Вы пропустили пароль") String password) {
}