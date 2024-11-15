package by.plamya.project.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.plamya.project.entity.PasswordResetToken;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.ResetTokenException;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.repository.PasswordTokenRepository;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.service.ResetTokenService;

@Service
public class ResetTokenServiceImpl implements ResetTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    // восстановление пароля
    @Override
    public String generateResetToken(String email) {

        User user = userRepository.findByEmail(email)
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

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken PASS_TOKEN = passwordTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResetTokenException("Token not found!"));

        return !isTokenFound(PASS_TOKEN) ? "invalidToken"
                : isTokenExpired(PASS_TOKEN) ? "expired"
                        : null;
    }

    @Override
    public boolean deleteToken(String token) {

        passwordTokenRepository.deleteByToken(token);
        return true;

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

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final LocalDateTime TIME = LocalDateTime.now();
        return passToken.getExpiryDate().isBefore(TIME);
    }
}
