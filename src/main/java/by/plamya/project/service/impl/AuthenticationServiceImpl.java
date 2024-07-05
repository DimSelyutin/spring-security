package by.plamya.project.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.AuthenticationException;

import by.plamya.project.entity.User;
import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.security.JWTTokenProvider;
import by.plamya.project.service.AuthenticationService;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.constants.SecurityConstants;
import by.plamya.project.utils.enums.ERole;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String USER_EMAIL_EXISTS = "User with this email already exists: ";
    private static final String USER_USERNAME_EXISTS = "User with this username already exists: ";
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Map<String, Object> login(LoginRequest loginRequest){

    
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.email(), loginRequest.password()));
        User user = userService.getCurrentUser(authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", jwt);
        return response;

    }

    @Transactional
    @Override
    public User registerUser(SignupRequest signupRequest) {
        validateUserData(signupRequest);

        User user = mapSignupRequestToUser(signupRequest);

        try {
            log.info("Saving user with email: " + user.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Error during registration", e);
            throw new UserExistException("User could not be saved. Please try again.");
        }
    }

    @Override
    public String activateUser(String code) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'activateUser'");
    }

    @Override
    public String getEmailByPasswordResetCode(String code) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEmailByPasswordResetCode'");
    }

    @Override
    public String sendPasswordResetCode(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendPasswordResetCode'");
    }

    @Override
    public String passwordReset(String email, String password, String password2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'passwordReset'");
    }

    private void validateUserData(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.username())) {
            throw new UserExistException(USER_USERNAME_EXISTS + signupRequest.username());
        }
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new UserExistException(USER_EMAIL_EXISTS + signupRequest.email());
        }
    }

    private User mapSignupRequestToUser(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.email());
        user.setUsername(signupRequest.username());
        user.setLastname(signupRequest.lastname());
        user.setPassword(bCryptPasswordEncoder.encode(signupRequest.password()));
        user.getRoles().add(ERole.ROLE_USER);
        return user;
    }
}
