package by.plamya.project.utils.validations;

import by.plamya.project.utils.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Проверка минимальной длины пароля
        if (password.length() < 8) {
            return false;
        }
        return true;
        // // Проверка наличия цифры и буквы в пароле
        // boolean containsDigit = false;
        // boolean containsLetter = false;

        // for (char c : password.toCharArray()) {
        // if (Character.isDigit(c)) {
        // containsDigit = true;
        // } else if (Character.isLetter(c)) {
        // containsLetter = true;
        // }

        // // Прерываем цикл, если оба условия выполнены
        // if (containsDigit && containsLetter) {
        // break;
        // }
        // }

        // return containsDigit && containsLetter;
    }
}
