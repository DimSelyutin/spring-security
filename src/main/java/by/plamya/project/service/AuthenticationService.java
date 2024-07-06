package by.plamya.project.service;

import java.util.Map;

import org.springframework.security.core.Authentication;

import by.plamya.project.dto.PasswordDTO;
import by.plamya.project.entity.User;
import by.plamya.project.entity.UserPrincipal;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.SignupRequest;

public interface AuthenticationService {

    Map<String, Object> login(LoginRequest loginRequest);

    User registerUser(SignupRequest signupRequest);

    User registerOauth2User(Authentication authentication);

    // User updateOauth2User(User user, String provider, OAuth2UserInfo
    // oAuth2UserInfo);

    String activateUser(String code);

    String sendPasswordResetCode(String email);

    String passwordReset(PasswordDTO passwordDTO);
}
