package by.plamya.project.service;

import java.util.List;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.payload.request.UpdateUserRequest;

public interface AdminService {

    List<UserDTO> getAllUsers();

    void createUser(SignupRequest createUserRequest);

    void updateUser(Long id, UpdateUserRequest updateUserRequest) throws Exception;

    void deleteUser(Long id) throws Exception;

    List<String> getAllRoles();

    void assignRoleToUser(Long id, String roleName) throws Exception;

}
