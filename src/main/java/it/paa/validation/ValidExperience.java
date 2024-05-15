package it.paa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExperienceValidator.class)
@Documented
public @interface ValidExperience {
    String message() default "The experience level is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}