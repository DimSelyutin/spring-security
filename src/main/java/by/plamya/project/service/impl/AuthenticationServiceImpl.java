package by.plamya.project.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import by.plamya.project.dto.PasswordDTO;
import by.plamya.project.entity.EmailDetails;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.ResetTokenException;
import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.exceptions.UserWithoutPasswordException;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.security.JWTTokenProvider;
import by.plamya.project.service.AuthenticationService;
import by.plamya.project.service.EmailService;
import by.plamya.project.service.ResetTokenService;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.constants.ResponseConstant;
import by.plamya.project.utils.constants.SecurityConstants;
import by.plamya.project.utils.enums.ERole;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private String hostname = "localhost:8080";

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private UserService userService;
    private JWTTokenProvider jwtTokenProvider;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ResetTokenService resetTokenService;
    private EmailService emailService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
            UserService userService, JWTTokenProvider jwtTokenProvider, BCryptPasswordEncoder bCryptPasswordEncoder,
            ResetTokenService resetTokenService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.resetTokenService = resetTokenService;
        this.emailService = emailService;
    }

    @Override
    public Map<String, Object> login(LoginRequest loginRequest) {

        Optional<User> opUser = userRepository.findByEmail(loginRequest.email());

        if (opUser.get().getPassword() == null) {
            sendPasswordResetCode(opUser.get().getEmail());
            throw new UserWithoutPasswordException("Check your email for set password! Email: " + loginRequest.email());
        }
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
            throw new UserExistException(ResponseConstant.USER_NOT_SAVE.toString());
        }
    }

    private void validateUserData(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new UserExistException(ResponseConstant.USER_EMAIL_EXISTS.toString() + signupRequest.email());
        }
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new UserExistException(ResponseConstant.USER_EMAIL_EXISTS.toString() + signupRequest.email());
        }
    }

    @Override
    public User registerOauth2User(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("oAuth obj {}", oAuth2User);
        String email = oAuth2User.getAttribute("email");
        String firstname = oAuth2User.getAttribute("name");
        String lastname = oAuth2User.getAttribute("family_name");
        String photo_link = oAuth2User.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, firstname, lastname, photo_link));

        return user;

    }

    @Override
    public String activateUser(String code) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'activateUser'");
    }

    @Override
    public String sendPasswordResetCode(String email) {
        // генерация токена
        String token = resetTokenService.generateResetToken(email);
        log.info("Sendin token on email: {}", email);
        EmailDetails emailDetails = new EmailDetails(email, "Yor token is:" + token,
                "Reset password Token", null);
        emailService.sendSimpleMail(emailDetails);
        log.info("Sending link on email for change password email: {}", email);
        return token;
    }

    @Override
    public String passwordReset(PasswordDTO passwordDTO) {
        String result = resetTokenService.validatePasswordResetToken(passwordDTO.getToken());

        if (result != null) {
            return result;
        }
        User user = userService.getUserByPasswordResetToken(passwordDTO.getToken());
        if (!user.getEmail().equals(passwordDTO.getEmail())) {
            throw new ResetTokenException("Check input data!");
        }
        userService.changeUserPassword(user, passwordDTO.getNewPassword());
        resetTokenService.deleteToken(passwordDTO.getToken());
        return result;
    }

    private User mapSignupRequestToUser(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.email());
        user.setFirstname(signupRequest.firstname());
        user.setLastname(signupRequest.lastname());
        user.setPhone(signupRequest.phone());
        user.setPassword(bCryptPasswordEncoder.encode(signupRequest.password()));
        user.getRoles().add(ERole.ROLE_USER);
        return user;
    }

    private User createUser(String email, String firstname, String lastname, String photoLink) {
        User user = new User();
        user.setEmail(email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPhotoLink(photoLink);
        user.setPhone("+375(99)999-99-99");
        user.getRoles().add(ERole.ROLE_USER);
        return userRepository.save(user);
    }
}
