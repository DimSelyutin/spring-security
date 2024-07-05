package by.plamya.project.service.impl;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.PasswordResetToken;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.ResetTokenException;
import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.payload.request.ChangePasswordRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.repository.PasswordTokenRepository;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.enums.ERole;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String USER_EMAIL_EXISTS = "User with this email already exists: ";
    private static final String USER_USERNAME_EXISTS = "User with this username already exists: ";
    private static final String TOKEN_NOT_FOUND = "Token not found";
    private static final String USERNAME_NOT_FOUND = "Username not found with username ";

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
            PasswordTokenRepository passwordTokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.passwordTokenRepository = passwordTokenRepository;
    }

    

    /**
     * Updates the username and lastname of the current user.
     *
     * @param userDTO   the new user data
     * @param principal the current user's principal
     * @return the updated user
     */
    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setUsername(userDTO.getUsername());
        user.setLastname(userDTO.getLastname());

        return userRepository.save(user);
    }

    /**
     * Retrieves a user by password reset token.
     *
     * @param token the password reset token
     * @return the user associated with the token
     */
    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken user = passwordTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResetTokenException(TOKEN_NOT_FOUND));

        return user.getUser();
    }

    /**
     * Updates the password of the current user.
     *
     * @param changePasswordRequest the password change request data
     * @param principal             the current user's principal
     * @return the updated user
     */
    public User updateUserPassword(ChangePasswordRequest changePasswordRequest, Principal principal) {
        User user = getUserByPrincipal(principal);
        validatePasswords(changePasswordRequest, user);
        String hashedNewPassword = bCryptPasswordEncoder.encode(changePasswordRequest.password());
        user.setPassword(hashedNewPassword);

        return saveUser(user);
    }

    /**
     * Changes the password of an unauthenticated user.
     *
     * @param user        the target user
     * @param newPassword the new password
     */
    public void changeUserPassword(User user, String newPassword) {
        String hashedNewPassword = bCryptPasswordEncoder.encode(newPassword);
        if (bCryptPasswordEncoder.matches(user.getPassword(), hashedNewPassword)) {
            throw new RuntimeException("New password is similar to the old one!");
        }
        user.setPassword(hashedNewPassword);
        saveUser(user);
    }

    /**
     * Retrieves the current authenticated user.
     *
     * @param principal the user's principal
     * @return the current authenticated user
     */
    public User getCurrentUser(Principal principal) {
        LOG.info("Log in Principal: {}", principal);
        User user = getUserByPrincipal(principal);
        initializeRoles(user);

        return user;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the user's ID
     * @return the retrieved user
     */
    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND + id));

        initializeRoles(user);

        return user;
    }

    

    /*
     * Utility methods
     */


    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND + username));
    }

    private void initializeRoles(User user) {
        if (user != null && user.getRoles() != null) {
            user.getRoles().size(); // Initializing roles collection
        }
    }

    private User saveUser(User user) {
        try {
            LOG.info("Saving/updating user with email: " + user.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            LOG.error("Error during saving/updating", e);
            throw new RuntimeException("User could not be saved. Please try again.");
        }
    }

    private void validatePasswords(ChangePasswordRequest changePasswordRequest, User user) {
        if (!bCryptPasswordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password!");
        }
        if (bCryptPasswordEncoder.matches(changePasswordRequest.password(), user.getPassword())) {
            throw new RuntimeException("The new password is similar to the old one!");
        }
    }



    // -----------------------------------------------
}
