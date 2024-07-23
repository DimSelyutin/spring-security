package by.plamya.project.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.payload.request.UpdateUserRequest;
import by.plamya.project.payload.response.MessageResponse;
import by.plamya.project.service.AdminService;
import by.plamya.project.utils.validations.ResponseErrorValidator;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ResponseErrorValidator responseErrorValidator;
    private final AdminService adminService;

    public AdminController(ResponseErrorValidator responseErrorValidator, AdminService adminService) {
        this.responseErrorValidator = responseErrorValidator;
        this.adminService = adminService;
    }

    // Получение списка всех пользователей
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Создание нового пользователя
    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody SignupRequest createUserRequest,
            BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        adminService.createUser(createUserRequest);
        return ResponseEntity.ok(new MessageResponse("User created successfully!"));
    }

    // Обновление информации о пользователе
    @PutMapping("/users/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest,
            BindingResult bindingResult) {

        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        try {
            adminService.updateUser(id, updateUserRequest);
            return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
        } catch (Exception ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));
        }
    }

    // Удаление пользователя по ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
        } catch (Exception ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));
        }
    }

    // Получение списка всех ролей
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = adminService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // Назначение роли пользователю
    @PostMapping("/users/{id}/role")
    public ResponseEntity<MessageResponse> assignRoleToUser(@PathVariable Long id,
            @RequestParam String role) {
        try {
            adminService.assignRoleToUser(id, role);
            return ResponseEntity.ok(new MessageResponse("Role assigned successfully!"));
        } catch (Exception ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));
        }
    }
}

// Описание эндпоинтов:
// GET /api/admin/users: Получить список всех пользователей.
// POST /api/admin/users: Создать нового пользователя.
// PUT /api/admin/users/{id}: Обновить информацию о пользователе.
// DELETE /api/admin/users/{id}: Удалить пользователя по ID.
// GET /api/admin/roles: Получить список всех ролей.
// POST /api/admin/users/{id}/role: Назначить роль пользователю.