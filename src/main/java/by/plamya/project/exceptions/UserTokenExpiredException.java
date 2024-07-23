package by.plamya.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserTokenExpiredException extends RuntimeException {
    public UserTokenExpiredException(String message) {
        super(message);
    }

}