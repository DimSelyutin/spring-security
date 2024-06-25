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

    // int min() default 0; // атрибут для минимальной длины email адреса

    // int max() default Integer.MAX_VALUE; // Добавляем атрибут для максимальной
    // длины email адреса

    // boolean allowLocal() default false; // атрибут для разрешения локальных
    // адресов (например, user@localhost)

}
