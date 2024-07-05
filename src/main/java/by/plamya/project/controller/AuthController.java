package by.plamya.project.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.plamya.project.dto.PasswordDTO;
import by.plamya.project.entity.PasswordResetToken;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.ResetTokenException;
import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.ResetPasswordRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.payload.response.JWTTokenSuccessResponse;
import by.plamya.project.payload.response.MessageResponse;
import by.plamya.project.security.JWTTokenProvider;
import by.plamya.project.service.AuthenticationService;
import by.plamya.project.service.ResetTokenService;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.constants.SecurityConstants;
import by.plamya.project.utils.validations.ResponseErrorValidator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("api/auth")
@PreAuthorize("permitAll()")
public class AuthController {
    public static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private ResponseErrorValidator responseErrorValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private ResetTokenService resetTokenService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        try {
            Map<String, Object> response = authenticationService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));
        }

    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest,
            BindingResult bindingResult) {

        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        try {
            authenticationService.registerUser(signupRequest);
        } catch (UserExistException ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));

        }
        return ResponseEntity.ok(new MessageResponse("User registered successful!"));
    }

    @PostMapping("/reset/password/complite")
    public ResponseEntity<Object> savePassword(final Locale locale, @Valid @RequestBody PasswordDTO passwordDTO) {

        try {
            String result = resetTokenService.validatePasswordResetToken(passwordDTO.getToken());

            if (result != null) {
                return ResponseEntity.ok(new MessageResponse("auth.message." + result));
            }
            User user = userService.getUserByPasswordResetToken(passwordDTO.getToken());
            if (!user.getEmail().equals(passwordDTO.getEmail())) {
                throw new ResetTokenException("Check input data!");
            }
            userService.changeUserPassword(user, passwordDTO.getNewPassword());
            resetTokenService.deleteToken(passwordDTO.getToken());
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("auth.message.invalid" + e.getLocalizedMessage()));

        }

        return ResponseEntity.ok(new MessageResponse("message.resetPasswordSuc"));

    }

    // запрос с проверкой востановления по емайл
    @PostMapping("/reset/password")
    public ResponseEntity<Object> resetUserPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest,
            BindingResult bindingResult) {

        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        try {
            // генерация токена
            String token = resetTokenService.generateResetToken(resetPasswordRequest);
            LOG.info("Sendin token on email: {}", resetPasswordRequest.email());
            // отправка сообщения для восстановления пароля
            // mailSender.send(constructResetTokenEmail(getAppUrl(request),
            // request.getLocale(), token, user));
            LOG.info("Sending link on email for change password email: {}", resetPasswordRequest.email());
        } catch (UserNotFoundException ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));

        }
        return ResponseEntity.ok(new MessageResponse("We are sent link for change password on your email!"));

    }
}
