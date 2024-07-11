package by.plamya.project.facade;

import org.springframework.stereotype.Component;

import by.plamya.project.dto.UserDTO;
import by.plamya.project.entity.User;

@Component
public class UserFacade {

    public UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLastname(user.getLastname());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

}