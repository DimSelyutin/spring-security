package by.plamya.project.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import by.plamya.project.entity.PasswordResetToken;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.ResetTokenException;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.payload.request.ResetPasswordRequest;
import by.plamya.project.repository.PasswordTokenRepository;
import by.plamya.project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResetTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    // восстановление пароля
    public String generateResetToken(ResetPasswordRequest resetPasswordRequest) {

        User user = userRepository.findByEmail(resetPasswordRequest.email())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        Optional<PasswordResetToken> passToken = passwordTokenRepository.findByUserId(user.getId());
        if (passToken.isPresent()) {
            String result = validatePasswordResetToken(passToken.get().getToken());
            if (result != null || result == "expired") {
                passwordTokenRepository.delete(passToken.get());
            } else {
                return passToken.get().getToken();
            }
        }
        createPasswordResetTokenForUser(user);
        return createPasswordResetTokenForUser(user).getToken();

    }

    /**
     * 
     * Utils
     * 
     **/
    private PasswordResetToken createPasswordResetTokenForUser(User user) {
        // passwordTokenRepository.findByUserId(user.getId());
        String token = UUID.randomUUID().toString();
        try {
            PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
            return passwordTokenRepository.save(passwordResetToken);
        } catch (Exception e) {
            throw new ResetTokenException(e.getMessage());
        }

    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken PASS_TOKEN = passwordTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResetTokenException("Token not found!"));

        return !isTokenFound(PASS_TOKEN) ? "invalidToken"
                : isTokenExpired(PASS_TOKEN) ? "expired"
                        : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final LocalDateTime TIME = LocalDateTime.now();
        return passToken.getExpiryDate().isBefore(TIME);
    }

    public boolean deleteToken(String token) {

        passwordTokenRepository.deleteByToken(token);
        return true;

    }
}
