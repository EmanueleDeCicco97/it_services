package it.paa.service;

import it.paa.model.Employee;
import it.paa.model.Technology;
import it.paa.repository.EmployeeTechnologyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class EmployeeTechnologyService implements EmployeeTechnologyRepository {

    @PersistenceContext
    EntityManager entityManager;
    @Inject
     EmployeeService employeeService;
    @Inject
    TechnologyService technologyService;


    @Override//metodo per associare un employee a una tecnologia
    @Transactional
    public void addEmployeeToTechnology(Long technologyId, Long employeeId) throws IllegalArgumentException, NotFoundException {
        // recupero technology e employee
        Employee employee = employeeService.findById(employeeId);
        Technology technology = technologyService.findById(technologyId);

        if (technology != null && employee != null) {
            //check se gia esiste una associazione tra employee e technology
            if (employee.getTechnologies().contains(technology)) {
                throw new EntityExistsException("This employee with this id already assign at this technology");
            }
            // check se il role dell'employee rispetta i requisiti dell'esperienza della tecnologia
            if (!technologyService.isEmployeeExperienceValid(technology.getRequiredExperienceLevel(), employee.getRole())) {
                throw new IllegalArgumentException("This employee does not satisfy requirement for experience level: " + technology.getRequiredExperienceLevel());
            }
            // aggiunge la technology all'elenco delle technologies di employee
            employee.addTechnology(technology);

            // aggiunge gli oggetti nella tabella di mezzo delle 2 classi
            entityManager.merge(employee);

            System.out.println("Employee successfully added to the technology.");
        }
    }
}
