package by.plamya.project.utils.validations;

import by.plamya.project.payload.request.PasswordMatchable;
import by.plamya.project.utils.annotations.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        PasswordMatchable uSignupRequest = (PasswordMatchable) obj;
        return uSignupRequest.getPassword().equals(uSignupRequest.getConfirmPassword());
    }

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

}
