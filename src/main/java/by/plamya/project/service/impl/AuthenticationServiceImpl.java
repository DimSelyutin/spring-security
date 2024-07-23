package by.plamya.project.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import by.plamya.project.dto.PasswordDTO;
import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.EmailDetails;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.ResetTokenException;
import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.exceptions.UserWithoutPasswordException;
import by.plamya.project.facade.UserFacade;
import by.plamya.project.payload.request.LoginRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.security.JWTTokenProvider;
import by.plamya.project.security.oauth.OAuth2UserInfo;
import by.plamya.project.service.AuthenticationService;
import by.plamya.project.service.EmailService;
import by.plamya.project.service.ResetTokenService;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.constants.ResponseConstant;
import by.plamya.project.utils.constants.SecurityConstants;
import by.plamya.project.utils.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private UserService userService;
    private JWTTokenProvider jwtTokenProvider;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ResetTokenService resetTokenService;
    private EmailService emailService;
    private UserFacade userFacade;

    @Override
    public Map<String, Object> login(LoginRequest loginRequest) {
        User user = validateAndAuthenticateUser(loginRequest);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", jwt);
        return response;
    }

    private User validateAndAuthenticateUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getPassword() == null) {
            sendPasswordResetCode(user.getEmail());
            throw new UserWithoutPasswordException(
                    "Check your email for set password! Email: " + loginRequest.email());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return userService.getCurrentUser(authentication);
    }

    @Transactional
    @Override
    public UserDTO registerUser(SignupRequest signupRequest) {
        User user = mapSignupRequestToUser(signupRequest);

        try {
            log.info("Saving user with email: " + user.getEmail());
            return userFacade.userToUserDTO(userService.createUser(user));
        } catch (Exception e) {
            log.error("Error during registration", e);
            throw new UserExistException(ResponseConstant.USER_NOT_SAVE.toString());
        }
    }

    @Override
    public User registerOauth2User(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("OAuth obj {}", oAuth2User);

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("name");
        String lastName = oAuth2User.getAttribute("family_name");
        String photoLink = oAuth2User.getAttribute("picture");

        return userService.createUser(email, firstName, lastName, photoLink);
    }

    @Override
    public String activateUser(String code) {
        throw new UnsupportedOperationException("Unimplemented method 'activateUser'");
    }

    @Override
    public String sendPasswordResetCode(String email) {
        String token = resetTokenService.generateResetToken(email);

        log.info("Sending token on email: {}", email);

        EmailDetails emailDetails = new EmailDetails(email, "Your token is: " + token,
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

    @Override
    @Transactional
    public User registerOauth2User(String provider, OAuth2UserInfo oAuth2UserInfo) {
        return createOrUpdateOauth2User(new User(), provider, oAuth2UserInfo);
    }

    @Override
    @Transactional
    public User updateOauth2User(User user, String provider, OAuth2UserInfo oAuth2UserInfo) {
        return createOrUpdateOauth2User(user, provider, oAuth2UserInfo);
    }

    private User createOrUpdateOauth2User(User user, String provider, OAuth2UserInfo oAuth2UserInfo) {
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setFirstname(oAuth2UserInfo.getFirstName());
        user.setLastname(oAuth2UserInfo.getLastName());

        // Устанавливаем активность пользователя и роли:
        user.setActive(1);

        user.setRoles(Collections.singleton(ERole.ROLE_USER));

        return userRepository.save(user);
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
}
