package by.plamya.project.service;

import by.plamya.project.entity.User;

public interface AdminUserService {

    User createUser(User user);

    User getUserById(Long id);

    User updateUser(Long id, User updatedUser);

    void deleteUser(Long id);

}
