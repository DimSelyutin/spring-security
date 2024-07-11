package by.plamya.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;
import by.plamya.project.facade.UserFacade;
import by.plamya.project.service.AdminUserService;
import by.plamya.project.utils.validations.ResponseErrorValidator;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/admin/user")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminUserService adminUserService;

    private final UserFacade userFacade;

    private final ResponseErrorValidator responseErrorValidator;

    public AdminController(AdminUserService adminUserService, UserFacade userFacade,
            ResponseErrorValidator responseErrorValidator) {
        this.adminUserService = adminUserService;
        this.userFacade = userFacade;
        this.responseErrorValidator = responseErrorValidator;
    }
//TODO admin methods
    @PostMapping("/update/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO,
            BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        // User updatedUser = adminUserService.updateUser(id, userDTO);

        // // Преобразовать пользователя в DTO (если необходимо)
        // UserDTO userUpdated = userFacade.userToUserDTO(updatedUser);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return new ResponseEntity<>("User with ID " + id + " has been deleted", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = adminUserService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        // User createdUser = adminUserService.createUser(userDTO);

        // // Преобразовать пользователя в DTO (если необходимо)
        // UserDTO userCreated = userFacade.userToUserDTO(createdUser);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}