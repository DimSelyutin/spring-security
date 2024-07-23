package by.plamya.project.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.facade.UserFacade;

import by.plamya.project.entity.User;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.payload.request.UpdateUserRequest;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.service.AdminService;
import by.plamya.project.utils.enums.ERole;
import io.jsonwebtoken.lang.Collections;
import lombok.Data;

@Service
@Data
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final UserFacade userFacade;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userFacade::userToUserDTO)
                .collect(Collectors.toList());
    }

    // @Override
    // public void createUser(SignupRequest createUserRequest) {
    // User newUser = new User(createUserRequest.firstname(),
    // createUserRequest.lastname(), createUserRequest.email(),
    // createUserRequest.password());
    // userRepository.save(newUser);
    // }

    @Override
    public void updateUser(Long id, UpdateUserRequest updateUserRequest) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("User not found"));

        if (updateUserRequest.getFirstname() != null) {
            user.setFirstname(updateUserRequest.getFirstname());
        }
        if (updateUserRequest.getLastname() != null) {
            user.setLastname(updateUserRequest.getLastname());
        }
        if (updateUserRequest.getEmail() != null) {
            user.setEmail(updateUserRequest.getEmail());
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<String> getAllRoles() {
        return Arrays.stream(ERole.values()).map(Enum::name).collect(Collectors.toList());
    }

    @Override
    public void assignRoleToUser(Long id, String roleName) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
        user.getRoles().add(ERole.valueOf(roleName.toUpperCase()));
        userRepository.save(user);
    }

    // @Override
    // public void updateUser(Long id, UpdateUserRequest updateUserRequest) throws Exception {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    // }

    @Override
    public void createUser(SignupRequest createUserRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }
}