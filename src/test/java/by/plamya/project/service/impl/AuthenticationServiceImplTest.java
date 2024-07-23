package by.plamya.project.service.impl;

import by.plamya.project.entity.User;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.exceptions.UserWithoutPasswordException;
import by.plamya.project.facade.UserFacade;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.security.JWTTokenProvider;
import by.plamya.project.service.EmailService;
import by.plamya.project.service.ResetTokenService;
import by.plamya.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private JWTTokenProvider jwtTokenProvider;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private ResetTokenService resetTokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Тест не найденого пользователя")
    @Test
    void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");

        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authenticationService.login(loginRequest));

        verify(userRepository).findByEmail(loginRequest.email());
        verifyNoMoreInteractions(userRepository, authenticationManager, userService, jwtTokenProvider);
    }

    @DisplayName("Тест пользователь без пароля")
    @Test
    void testLogin_UserWithoutPassword() {
        String email = "user@example.com";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UserWithoutPasswordException.class, () -> authenticationService.login(loginRequest));

        verify(userRepository).findByEmail(email);
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    @DisplayName("Тест успешной регистрации")

    @Test
    void testRegisterUserSuccess() {
        SignupRequest signupRequest = new SignupRequest("test@example.com", "Test", "User", "1234567890", "password", "password");
        authenticationService.registerUser(signupRequest);

        verify(userService, times(1)).createUser(any(User.class));
    }

    @DisplayName("Тест успешного входа")
    @Test
    void testLogin_Successful() {
        String email = "user@example.com";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.getCurrentUser(authentication)).thenReturn(user);
        when(jwtTokenProvider.generateToken(user)).thenReturn("test-jwt-token");

        Map<String, Object> response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals(user, response.get("user"));
        assertEquals("Bearer test-jwt-token", response.get("token"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @DisplayName("Тест отправки токена на почту")
    @Test
    public void testSendPasswordResetCode() {
        String email = "test@test.com";
        String token = "reset-token";

        when(resetTokenService.generateResetToken(email)).thenReturn(token);

        String result = authenticationService.sendPasswordResetCode(email);

        assertEquals(token, result);
        verify(resetTokenService).generateResetToken(email);

    }
}