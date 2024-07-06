package by.plamya.project.security.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import by.plamya.project.entity.User;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.security.JWTTokenProvider;
import by.plamya.project.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler

{

    private JWTTokenProvider jwtProvider;
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private AuthenticationService authenticationService;

    public OAuth2SuccessHandler(JWTTokenProvider jwtProvider, UserRepository userRepository,
            CustomOAuth2UserService customOAuth2UserService) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;

    }

    private String hostname = "localhost:8080";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            log.info("oAuth obj {}", oAuth2User);
            User user = authenticationService.registerOauth2User(authentication);
            String token = jwtProvider.generateToken(user);
            String redirectUri = buildRedirectUri(token);

            getRedirectStrategy().sendRedirect(request, response, redirectUri);
        } catch (Exception e) {
            log.error("OAuth2 error during authentication: {}", e.getMessage());
        }
    }

    private String buildRedirectUri(String token) {
        return UriComponentsBuilder.fromUriString("http://" + hostname + "/oauth2/redirect")
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}