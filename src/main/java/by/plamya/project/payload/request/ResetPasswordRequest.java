package by.plamya.project.payload.request;

import jakarta.validation.constraints.NotEmpty;

public record ResetPasswordRequest(
        @NotEmpty(message = "Вы пропустили логин") String email) {
}