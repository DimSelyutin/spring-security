package by.plamya.project.service;

import java.util.Map;

import by.plamya.project.entity.User;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.SignupRequest;

public interface AuthenticationService {

    Map<String, Object> login(LoginRequest loginRequest);

    User registerUser(SignupRequest signupRequest);

    // User registerOauth2User(String provider, OAuth2UserInfo oAuth2UserInfo);

    // User updateOauth2User(User user, String provider, OAuth2UserInfo
    // oAuth2UserInfo);

    String activateUser(String code);

    String getEmailByPasswordResetCode(String code);

    String sendPasswordResetCode(String email);

    String passwordReset(String email, String password, String password2);
}
