package by.plamya.project.service;

public interface ResetTokenService {

    String generateResetToken(String email);

    String validatePasswordResetToken(String token);

    boolean deleteToken(String token);
}
