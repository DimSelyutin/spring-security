package by.plamya.project.service.impl;

import by.plamya.project.entity.User;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.security.JWTTokenProvider;
import by.plamya.project.security.oauth.OAuth2UserInfo;
import by.plamya.project.service.AuthenticationService;
import by.plamya.project.security.oauth.OAuth2UserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final JWTTokenProvider jwtProvider;
    private final UserRepository userRepository;

    @Autowired
    @Lazy
    private AuthenticationService authenticationService;

    public String handleOAuth2Success(Authentication authentication) {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            log.info("OAuth2 user authenticated with provider: {}", oAuth2User);

            String provider = "google";
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

            String email = oAuth2UserInfo.getEmail();
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user = new User();

            if (userOptional.isPresent()) {
                user = authenticationService.updateOauth2User(userOptional.get(), provider, oAuth2UserInfo);
            } else {
                user = authenticationService.registerOauth2User(provider, oAuth2UserInfo);
            }

            return jwtProvider.generateToken(user);

        } catch (Exception e) {
            log.error("OAuth2 error during authentication: {}", e.getMessage());
            throw new RuntimeException("OAuth2 authentication failed", e);
        }
    }
}