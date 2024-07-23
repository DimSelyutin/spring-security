package by.plamya.project.facade;

import org.springframework.stereotype.Component;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;

@Component
public class UserFacade {

    public UserDTO userToUserDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail());
    }

}