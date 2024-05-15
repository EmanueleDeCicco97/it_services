package it.paa.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateCheckValidatorDto.class)


@Documented
public @interface DateCheckDto {
    String message() default "The end date of the project must be later than the start date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}