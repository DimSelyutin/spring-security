package by.plamya.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.constraints.NotEmpty;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserWithoutPasswordException extends RuntimeException {
    public UserWithoutPasswordException(String message) {
        super(message);
    }
    
}
