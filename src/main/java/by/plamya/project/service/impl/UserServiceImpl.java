package by.plamya.project.service.impl;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.PasswordResetToken;
import by.plamya.project.entity.User;
import by.plamya.project.exceptions.ResetTokenException;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.facade.UserFacade;
import by.plamya.project.payload.request.ChangePasswordRequest;
import by.plamya.project.repository.PasswordTokenRepository;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.enums.ERole;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String TOKEN_NOT_FOUND = "Token not found";
    private static final String USERNAME_NOT_FOUND = "Username not found with username ";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PasswordTokenRepository passwordTokenRepository;
    private final UserFacade userFacade;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USERNAME_NOT_FOUND));
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
        user.setFirstname(userDTO.getUsername());
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
        User user = getUserByPrincipal(principal);
        initializeRoles(user);

        return user;
    }

    @Override
    public User createUser(User user) {
        // Проверка уникальности email
        userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь с таким email уже существует"));

        // Хэширование пароля перед сохранением
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        } else {
            throw new RuntimeException("Пароль не может быть пустым");
        }

        // Установка стандартной роли, если роли не заданы
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(ERole.ROLE_USER)); // Замените ERole на ваш класс ролей
        }

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream().map(userFacade::userToUserDTO)
                .collect(Collectors.toList());

        return users;
    }

    @Override
    public User createUser(String email, String firstname, String lastname, String photoLink) {
        // Проверка уникальности email
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь с таким email уже существует"));

        User user = new User();
        user.setEmail(email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPhotoLink(photoLink);
        user.setPhone("");

        // Установка ролей пользователя
        user.getRoles().add(ERole.ROLE_USER);

        // Установка дополнительных полей
        user.setActive(1); // или другое значение в зависимости от логики
        user.setEmailVerify(0); // или другое значение в зависимости от логики
        user.setCreatedTime(LocalDateTime.now());

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserInfo(String email) {
        return userRepository.findByEmail(email);
    }
    /*
     * Utility methods
     */

    private User getUserByPrincipal(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND + email));
    }

    private void initializeRoles(User user) {
        Optional.ofNullable(user)
                .ifPresent(u -> u.getRoles().size()); // Initializing roles collection
    }

    private User saveUser(User user) {
        try {
            LOG.info("Saving/updating user with email: " + user.getEmail());
            return Optional.ofNullable(user)
                    .map(userRepository::save)
                    .orElseThrow(() -> new RuntimeException("User could not be saved. Please try again."));
        } catch (Exception e) {
            LOG.error("Error during saving/updating", e);
            throw new RuntimeException("User could not be saved. Please try again.");
        }
    }

    // -----------------------------------------------

    private void validatePasswords(ChangePasswordRequest changePasswordRequest, User user) {
        if (!bCryptPasswordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid old password!");
        }
        if (bCryptPasswordEncoder.matches(changePasswordRequest.password(), user.getPassword())) {
            throw new RuntimeException("The new password is similar to the old one!");
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(TOKEN_NOT_FOUND));
    }
}
