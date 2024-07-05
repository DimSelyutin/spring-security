package by.plamya.project.service;

import java.security.Principal;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;
import by.plamya.project.entity.UserPrincipal;
import by.plamya.project.payload.request.ChangePasswordRequest;
import by.plamya.project.payload.request.SignupRequest;

public interface UserService {
    User createUser(SignupRequest signupRequest);

    User updateUser(UserDTO userDTO, Principal principal);

    User getUserByPasswordResetToken(String token);

    User updateUserPassword(ChangePasswordRequest changePasswordRequest, Principal principal);

    void changeUserPassword(User user, String newPassword);

    User getCurrentUser(Principal principal);

    User getUserById(Long id);

}
