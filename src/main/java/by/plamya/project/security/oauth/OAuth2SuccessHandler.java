package by.plamya.project.security.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import by.plamya.project.entity.User;
import by.plamya.project.payload.response.JWTTokenSuccessResponse;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.security.JWTTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler
extends SimpleUrlAuthenticationSuccessHandler

{

    @Autowired
    private JWTTokenProvider jwtProvider;
    private String hostname = "localhost:8080";
    @Autowired
    private UserRepository userRepository;

    // @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");
        String lastname = oAuth2User.getAttribute("family_name");

        Optional<User> existingUserOptional = userRepository.findByEmail(email);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            // Обновление информации о пользователе, если он уже существует
            existingUser.setUsername(username);
            existingUser.setLastname(lastname);
            userRepository.save(existingUser);
        } else {
            // Создание нового пользователя, если его нет в базе данных
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setLastname(lastname);
            userRepository.save(newUser);
        }

        // Получение пользователя после сохранения
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Failed to save user"));

        // Генерация токена JWT для пользователя
        String token = jwtProvider.generateToken(user);

        // Генерация URI с токеном для редиректа
        String uri = UriComponentsBuilder.fromUriString("http://" + hostname + "/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();

        // Отправка редиректа с URI содержащим токен
        getRedirectStrategy().sendRedirect(request, response, uri);
    }
}