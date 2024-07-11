package by.plamya.project.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import by.plamya.project.entity.User;
import by.plamya.project.exceptions.UserNotFoundException;
import by.plamya.project.repository.UserRepository;
import by.plamya.project.service.AdminUserService;
import by.plamya.project.utils.enums.ERole;

import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final String USERNAME_NOT_FOUND = "Username not found with username ";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new UserNotFoundException(USERNAME_NOT_FOUND));
    }

    @Override
    public User createUser(User user) {
        // Проверка уникальности email
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

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
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USERNAME_NOT_FOUND));
        userRepository.delete(existingUser);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USERNAME_NOT_FOUND));
        // Обновление полей пользователя, кроме идентификатора и email
        existingUser.setFirstname(updatedUser.getFirstname());
        existingUser.setLastname(updatedUser.getLastname());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setActive(updatedUser.getActive());
        existingUser.setEmailVerify(updatedUser.getEmailVerify());
        existingUser.setPhotoLink(updatedUser.getPhotoLink());

        // Если пароль был изменен, хэшируем его перед сохранением
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
        }

        // Обновление ролей пользователя
        existingUser.setRoles(updatedUser.getRoles());

        return userRepository.save(existingUser);
    }
}
