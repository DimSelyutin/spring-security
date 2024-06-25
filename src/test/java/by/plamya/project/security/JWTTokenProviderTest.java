package by.plamya.project.security;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import by.plamya.project.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class JWTTokenProviderTest {

    // Тест генерации JWT токена
    // @Test
    // public void generateTokenTest() {
    //     // Подготовка данных для теста
    //     User user = new User(1L, "test@test.com", "testuser", "password", null);
    //     Authentication authentication = Mockito.mock(Authentication.class);
    //     Mockito.when(authentication.getPrincipal()).thenReturn(user);

    //     // Создание объекта JWTTokenProvider
    //     JWTTokenProvider tokenProvider = new JWTTokenProvider();

    //     // Вызов метода generateToken
    //     String token = tokenProvider.generateToken(authentication);
    //     // Проверка результата
    //     assertNotNull(token); // Проверка, что токен сгенерирован успешно
    // }

    // // Тест валидации токена
    // @Test
    // public void validateTokenTest() {
    //     // Подготовка данных для теста
    //     User user = new User(1L, "test@test.com", "testuser", "password", null);
    //     Authentication authentication = Mockito.mock(Authentication.class);
    //     Mockito.when(authentication.getPrincipal()).thenReturn(user);
    //     // Создание объекта JWTTokenProvider
    //     JWTTokenProvider tokenProvider = new JWTTokenProvider();

    //     String validToken = tokenProvider.generateToken(authentication);

    //     assertTrue(tokenProvider.validateToken(validToken)); // Проверка на успешную валидацию токена
    // }

    // // Тест извлечения идентификатора пользователя из токена
    // @Test
    // public void getUserIdFromTokenTest() {
    //     // Подготовка данных для теста
    //     User user = new User(123456L, "test@test.com", "testuser", "password", null);
    //     Authentication authentication = Mockito.mock(Authentication.class);
    //     Mockito.when(authentication.getPrincipal()).thenReturn(user);
    //     // Создание объекта JWTTokenProvider
    //     JWTTokenProvider tokenProvider = new JWTTokenProvider();
    //     String validToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWQiOiIxIiwiZW1haWwiOiJkaW1rYTJAbWFpbC5ydSIsInVzZXJuYW1lIjoidmFudXNoa2EyIiwibGFzdG5hbWUiOm51bGwsImlhdCI6MTcxODU2MDkxNSwiZXhwIjoxNzE4NTYxNTE1fQ.FHSzCcNzsbJILpyIiL8g1YlqrXGK9muQ2BEEeo0TzryNrfk6Co4eL7ISWS7VitEeQTgcK-vlmWY5l8c5aqHeoQ";

    //     Long userId = tokenProvider.getUserIdFromToken(validToken);

    //     assertEquals(123456L, userId); // Проверка успешного извлечения идентификатора
    // }
}