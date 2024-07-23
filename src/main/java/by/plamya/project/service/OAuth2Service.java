package by.plamya.project.service;

import org.springframework.security.core.Authentication;

public interface OAuth2Service {
    String handleOAuth2Success(Authentication authentication);

}