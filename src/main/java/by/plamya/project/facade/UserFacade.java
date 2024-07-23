package by.plamya.project.facade;

import org.springframework.stereotype.Component;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;
import org.springframework.util.StringUtils;

@Component
public class UserFacade {

    public UserDTO userToUserDTO(User user) {
        validateUser(user);

        Long id = getId(user);
        String firstname = getFirstName(user);
        String lastname = getLastName(user);
        String email = getEmail(user);

        return new UserDTO(id, firstname, lastname, email);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
    }

    private Long getId(User user) {
      
        return user.getId();
    }

    private String getFirstName(User user) {
        return StringUtils.hasText(user.getFirstname()) ? user.getFirstname() : "Unknown";
    }

    private String getLastName(User user) {
        return StringUtils.hasText(user.getLastname()) ? user.getLastname() : "Unknown";
    }

    private String getEmail(User user) {
        return StringUtils.hasText(user.getEmail()) ? user.getEmail() : "unknown@example.com";
    }
}