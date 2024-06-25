package by.plamya.project.utils.validations;

import by.plamya.project.payload.request.SignupRequest;
import by.plamya.project.utils.annotations.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        SignupRequest uSignupRequest = (SignupRequest) obj;
        return uSignupRequest.password().equals(uSignupRequest.confirmPassword());
    }

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        
    }

}
