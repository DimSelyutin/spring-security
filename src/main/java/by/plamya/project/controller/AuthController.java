package by.plamya.project.controller;

import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import by.plamya.project.dto.PasswordDTO;
import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.exceptions.UserWithoutPasswordException;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.ResetPasswordRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.payload.response.MessageResponse;
import by.plamya.project.service.AuthenticationService;
import by.plamya.project.utils.validations.ResponseErrorValidator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@CrossOrigin
@RequestMapping("api/auth")
@RestController
@AllArgsConstructor
@PreAuthorize("permitAll()")
public class AuthController {

    private ResponseErrorValidator responseErrorValidator;
    private AuthenticationService authenticationService;

    
    @RequestMapping(value = "/signin", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors))
            return errors;
        try {
            Map<String, Object> response = authenticationService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException | UserNotFoundException | UserWithoutPasswordException ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));
        }

    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest,
            BindingResult bindingResult) {

        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors))
            return errors;

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
            authenticationService.passwordReset(passwordDTO);
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Reset password invalid" + e.getLocalizedMessage()));

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
            authenticationService.sendPasswordResetCode(resetPasswordRequest.email());
        } catch (UserNotFoundException ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));

        }
        return ResponseEntity.ok(new MessageResponse("We are sent link for change password on your email!"));

    }
}
