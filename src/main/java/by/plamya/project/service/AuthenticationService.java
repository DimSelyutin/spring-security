package by.plamya.project.service;

import java.util.Map;

import org.springframework.security.core.Authentication;

import by.plamya.project.dto.PasswordDTO;
import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.security.oauth.OAuth2UserInfo;

public interface AuthenticationService {

    Map<String, Object> login(LoginRequest loginRequest);

    UserDTO registerUser(SignupRequest signupRequest);

    User registerOauth2User(Authentication authentication);

    User registerOauth2User(String provider, OAuth2UserInfo oAuth2UserInfo);

    User updateOauth2User(User user, String provider, OAuth2UserInfo oAuth2UserInfo);

    String activateUser(String code);

    String sendPasswordResetCode(String email);

    String passwordReset(PasswordDTO passwordDTO);
}
