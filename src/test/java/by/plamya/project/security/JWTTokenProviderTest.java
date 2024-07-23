package by.plamya.project.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.springframework.security.core.Authentication;
import by.plamya.project.entity.User;
import by.plamya.project.utils.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class JWTTokenProviderTest {

    private User user = new User();
    {
        user.setId(1L);
        user.setEmail("test@test.com");
    }

    @DisplayName("Тест генерации JWT токена")
    // @Disabled
    @Test
    public void generateTokenTest() {
        // Подготовка данных для теста

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);

        // Создание объекта JWTTokenProvider
        JWTTokenProvider tokenProvider = new JWTTokenProvider();

        // Вызов метода generateToken
        String token = tokenProvider.generateToken(user);
        // Проверка результата
        assertNotNull(token); // Проверка, что токен сгенерирован успешно
    }

    @DisplayName("Тест валидации токена")

    // @Disabled
    @Test
    public void validateTokenTest() {
        // Подготовка данных для теста
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        // Создание объекта JWTTokenProvider
        JWTTokenProvider tokenProvider = new JWTTokenProvider();

        String validToken = tokenProvider.generateToken(user);

        assertTrue(tokenProvider.validateToken(validToken)); // Проверка на успешную валидацию токена
    }

    @DisplayName("Извлечения ID пользователя из токена")
    // @Disabled
    @Test
    public void getUserIdFromTokenTest() {
        // Подготовка данных для теста
        user.setId(123456L);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        // Создание объекта JWTTokenProvider
        JWTTokenProvider tokenProvider = new JWTTokenProvider();
        String validToken = tokenProvider.generateToken(user);
        // String validToken =
        // "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwiZW1haWwiOiJkaW1rYTJAbWFpbC5ydSIsInVzZXJuYW1lIjoidmFudXNoa2EyIiwibGFzdG5hbWUiOm51bGwsImlhdCI6MTcxODU2MDkxNSwiZXhwIjoxNzE4NTYxNTE1fQ.FHSzCcNzsbJILpyIiL8g1YlqrXGK9muQ2BEEeo0TzryNrfk6Co4eL7ISWS7VitEeQTgcK-vlmWY5l8c5aqHeoQ";

        Long userId = tokenProvider.getUserIdFromToken(validToken);

        assertEquals(123456L, userId); // Проверка успешного извлечения идентификатора
    }

    @DisplayName("Отправка истекшего токена")
    @Test
    public void testExpiredToken() {
        // Подготовка данных для теста
        JWTTokenProvider tokenProvider = new JWTTokenProvider();
        String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwiZW1haWwiOiJkaW1rYTJAbWFpbC5ydSIsInVzZXJuYW1lIjoidmFudXNoa2EyIiwibGFzdG5hbWUiOm51bGwsImlhdCI6MTcxODU2MDkxNSwiZXhwIjoxNzE4NTYxNTE1fQ.FHSzCcNzsbJILpyIiL8g1YlqrXGK9muQ2BEEeo0TzryNrfk6Co4eL7ISWS7VitEeQTgcK-vlmWY5l8c5aqHeoQ";

        boolean isTokenExpired = checkIfTokenExpired(tokenProvider, expiredToken);

        assertTrue(isTokenExpired, "Токен должен быть истекшим");
    }

    private boolean checkIfTokenExpired(JWTTokenProvider tokenProvider, String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET).parseClaimsJws(token).getBody();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при проверке токена: " + e.getMessage());
            return false;
        }
    }
}