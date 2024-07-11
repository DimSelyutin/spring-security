package by.plamya.project.controller;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;
import by.plamya.project.facade.UserFacade;
import by.plamya.project.payload.request.ChangePasswordRequest;
import by.plamya.project.payload.response.MessageResponse;
import by.plamya.project.service.UserService;
import by.plamya.project.utils.validations.ResponseErrorValidator;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private ResponseErrorValidator responseErrorValidator;

    public static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {

        User user = userService.getCurrentUser(principal);
        UserDTO userDTO = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    
    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult,
            Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors))
            return errors;

        User user = userService.updateUser(userDTO, principal);

        UserDTO userUpdated = userFacade.userToUserDTO(user);
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    @PostMapping("/update/password")
    public ResponseEntity<Object> updateUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            BindingResult bindingResult,
            Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }
        try {
            userService.updateUserPassword(changePasswordRequest, principal);
        } catch (RuntimeException ex) {
            return ResponseEntity.ok(new MessageResponse(ex.getMessage()));

        }

        return ResponseEntity.ok(new MessageResponse("Your password changed successful!"));

    }

    // @GetMapping("/changePassword")
    // public String redirectChangePasswordPage(Locale locale, Model model,
    // @RequestParam("token") String token) {

    // String result = userService.validatePasswordResetToken(token);
    // if (result != null) {
    // String message = messages.getMessage("auth.message." + result, null, locale);
    // return "redirect:/login.html?lang="
    // + locale.getLanguage() + "&message=" + message;
    // } else {
    // model.addAttribute("token", token);
    // return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    // }
    // }

}
