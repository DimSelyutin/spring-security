package by.plamya.project.service;

import java.security.Principal;
import java.util.List;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;
import by.plamya.project.payload.request.ChangePasswordRequest;

public interface UserService {

    List<User> getAllUsers();

    User createUser(User user);

    User getUserById(Long id);

    User createUser(String email, String firstname, String lastname, String photoLink);

    User getUserByPasswordResetToken(String token);

    User updateUser(UserDTO userDTO, Principal principal);

    User updateUserPassword(ChangePasswordRequest changePasswordRequest, Principal principal);

    void changeUserPassword(User user, String newPassword);

    User getCurrentUser(Principal principal);
}