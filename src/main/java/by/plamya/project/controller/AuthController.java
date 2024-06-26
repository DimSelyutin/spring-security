package by.plamya.project.controller;

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

import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.payload.response.JWTTokenSuccessResponse;
import by.plamya.project.payload.response.MessageResponse;
import by.plamya.project.security.JWTTokenProvider;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.constants.SecurityConstants;
import by.plamya.project.utils.validations.ResponseErrorValidator;
import jakarta.validation.Valid;

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
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.username(),
                    loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

            return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
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
            userService.createUser(signupRequest);
        } catch (UserExistException ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));

        }
        return ResponseEntity.ok(new MessageResponse("User registered successful!"));
    }
}
