package it.paa.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExperienceValidator implements ConstraintValidator<ValidExperience, String> {

    @Override
    public void initialize(ValidExperience constraintAnnotation) {
    }

    @Override
    public boolean isValid(String experienceLevel, ConstraintValidatorContext context) {
        if (experienceLevel == null) {
            return false; // Trattamento per valore null
        }

        // Controllo se il livello di esperienza è uno tra junior, middle o senior
        return isExperienceValid(experienceLevel);
    }

    // Metodo per il controllo della validità del livello di esperienza
    private boolean isExperienceValid(String experienceLevel) {
        return experienceLevel.equalsIgnoreCase("junior") ||
                experienceLevel.equalsIgnoreCase("middle") ||
                experienceLevel.equalsIgnoreCase("senior") ||
                experienceLevel.equalsIgnoreCase("project manager");
    }
}
