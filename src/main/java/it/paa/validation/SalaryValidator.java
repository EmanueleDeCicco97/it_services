package it.paa.validation;

import it.paa.dto.EmployeeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SalaryValidator implements ConstraintValidator<ValidSalary, EmployeeDto> {

    @Override
    public void initialize(ValidSalary constraintAnnotation) {
    }

    @Override
    public boolean isValid(EmployeeDto employee, ConstraintValidatorContext context) {
        if (employee == null) {
            return true; // Trattamento per oggetto null
        }

        // Estraiamo il ruolo e lo stipendio dal dipendente
        String role = employee.getRole();
        double salary = employee.getSalary();

        // Controllo sullo stipendio in base al ruolo
        if (!isSalaryValid(role, salary)) {
            return false; // Lo stipendio non è congruo per il ruolo
        }

        return true; // Stipendio congruo
    }

    // Metodo per il controllo della congruenza dello stipendio in base al ruolo
    private boolean isSalaryValid(String role, double salary) {
        switch (role.toLowerCase()) {
            case "senior":
                // Lo stipendio minimo per un senior è minimo 2500 o superiore
                return salary >= 2500;
            case "junior":
                // Lo stipendio minimo per un junior è minimo 1500 o superiore
                return salary >= 1500;
            case "middle":
                // Lo stipendio minimo per un middle è minimo 2000 o superiore
                return salary >= 2000;
            case "project manager":
                // Lo stipendio minimo per un project manager è minimo 2000 o superiore
                return salary >= 3000;
            default:
                return false; // Ruolo non riconosciuto
        }
    }
}

