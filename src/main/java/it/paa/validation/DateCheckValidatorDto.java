package it.paa.validation;

import it.paa.dto.ProjectDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateCheckValidatorDto implements ConstraintValidator<DateCheckDto, ProjectDto> {

    @Override
    public void initialize(DateCheckDto constraintAnnotation) {
    }

    @Override //metodo per controllare se la data di fine è successiva a quella di inizio
    public boolean isValid(ProjectDto projectDto, ConstraintValidatorContext context) {
        if (projectDto == null) {
            return true;
        }

        if (projectDto.getStartDate() == null || projectDto.getEndDate() == null) {
            return true;
        }

        return projectDto.getEndDate().isAfter(projectDto.getStartDate());
    }
}