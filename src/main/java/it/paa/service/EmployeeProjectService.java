package it.paa.service;

import it.paa.model.Employee;
import it.paa.model.Project;
import it.paa.repository.EmployeeProjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EmployeeProjectService implements EmployeeProjectRepository {
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
     EmployeeService employeeService;
    @Inject
    ProjectService projectService;


    // metodo per aggiungere un employee al project
    @Transactional
    @Override
    public void addEmployeeToProject(Long projectId, Long employeeId) {

        // recupero i dati dal database
        Employee employee = employeeService.findById(employeeId);
        Project project = projectService.findById(projectId);
        // controllo se sono diversi da null
        if (project != null && employee != null) {

            // se già esiste un associazione mando l'errore
            if (employee.getProjects().contains(project)) {
                throw new EntityExistsException("This employee with this id already assign at this project");
            }
            // aggiungo il progetto all'elenco dei project di employee.
            employee.addProject(project);

            // aggiunge gli oggetti nella tabella di mezzo delle 2 classi
            entityManager.merge(project);

        }
    }
}
