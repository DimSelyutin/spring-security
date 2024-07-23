package by.plamya.project.utils.annotations;

import by.plamya.project.utils.validations.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    

    String message()

    default "Неверный Email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload()default{};



}
