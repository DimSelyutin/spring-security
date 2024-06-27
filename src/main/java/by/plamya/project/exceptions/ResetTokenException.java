package by.plamya.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResetTokenException extends RuntimeException {
    public ResetTokenException(String message) {
        super(message);
    }
}
