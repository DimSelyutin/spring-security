package by.plamya.project.service;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.UserExistException;
import by.plamya.project.payload.request.ChangePasswordRequest;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.utils.enums.ERole;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Создает нового пользователя на основе данных из запроса.
     *
     * @param signupRequest данные для создания нового пользователя
     * @return созданный пользователь
     * @throws UserExistException если пользователь с таким username или email уже
     *                            существует
     */
    @Transactional
    public User createUser(SignupRequest signupRequest) {
        validateUserData(signupRequest);

        User user = new User();
        user.setEmail(signupRequest.email());
        user.setUsername(signupRequest.username());
        user.setLastname(signupRequest.lastname());
        user.setPassword(bCryptPasswordEncoder.encode(signupRequest.password()));
        user.getRoles().add(ERole.ROLE_USER);

        try {
            LOG.info("Saving user with email: " + user.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            LOG.error("Error during registration", e);
            throw new UserExistException("User could not be saved. Please try again.");
        }
    }

    // смена имени и фамилии
    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setUsername(userDTO.getUsername());
        user.setLastname(userDTO.getLastname());

        return userRepository.save(user);
    }

    // смена паролья
    public User updateUserPassword(ChangePasswordRequest changePasswordRequest, Principal principal) {
        User user = getUserByPrincipal(principal);
        String oldPassword = changePasswordRequest.oldPassword();
        String newPassword = changePasswordRequest.password();

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid old password!");
        }
        if (!bCryptPasswordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("The new password is similar to the old one!");
        }
        String hashedNewPassword = bCryptPasswordEncoder.encode(newPassword);
        user.setPassword(hashedNewPassword);
        try {
            LOG.info("Update user password with email: " + user.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            LOG.error("Error during change pass", e);
            throw new RuntimeException("User could not be saved. Please try again.");
        }

    }

    // получение пользователя из принципал
    public User getCurrentUser(Principal principal) {
        LOG.info("ogj in Principal: {}", principal);
        User user = getUserByPrincipal(principal);
        initializeRoles(user);

        return user;
    }

    // получение пользователя по id
    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        initializeRoles(user);

        return user;
    }

    /**
     * 
     * Utils
     * 
     **/

    private void validateUserData(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.username())) {
            throw new UserExistException("Username already exists: " + signupRequest.username());
        }

        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new UserExistException("Email already exists: " + signupRequest.email());
        }
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }

    private void initializeRoles(User user) {
        if (user != null && user.getRoles() != null) {
            user.getRoles().size(); // Initializing roles collection
        }
    }
}
