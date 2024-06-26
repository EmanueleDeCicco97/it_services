package it.paa.service;

import it.paa.dto.EmployeeDto;
import it.paa.model.Employee;
import it.paa.model.Project;
import it.paa.model.Technology;
import it.paa.repository.EmployeeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class EmployeeService implements EmployeeRepository {

    @PersistenceContext
    private EntityManager em;

    // Esercitazioni avanzate (facoltative)
    // Esercitazione 1: Realizzare una funzionalità per ottenere tutti i progetti in cui è coinvolto
    // un dipendente specifico e visualizzare i dettagli dei progetti e dei clienti coinvolti.
    @Override // metodo per mostra i clienti e i dettagli dei progetti associati al dipendente inserito
    public Employee findById(Long id) throws NotFoundException {
        Employee employee = em.find(Employee.class, id);

        if (employee == null) {
            throw new NotFoundException("Employee not found with id: " + id);
        }
        return employee;
    }

    //restituisce il singolo employee senza le classi associate
//    public EmployeeDto findByIdDto(Long id)throws NotFoundException {
//        Employee employee = em.find(Employee.class, id);
//
//        if (employee == null) {
//            throw new NotFoundException("Employee not found with id: " + id);
//        }
//        EmployeeDto employeeDto = new EmployeeDto();
//
//        employeeDto.setName(employee.getName());
//        employeeDto.setSurname(employee.getSurname());
//        employeeDto.setSalary(employee.getSalary());
//        employeeDto.setRole(employee.getRole());
//        employeeDto.setHireDate(employee.getHireDate());
//
//        return  employeeDto;
//
//    }

    //restituisce l'employee in base al nome e cognome senza le associazioni
    public List<EmployeeDto> findAllByAttributes(String name, String surname) {
        List<Employee> employees = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
        if (name != null && !name.isEmpty() && !name.isBlank() && surname != null && !surname.isEmpty() && !surname.isBlank()) {
            employees = employees.stream()
                    .filter(employee -> employee.getName().equalsIgnoreCase(name))
                    .filter(employee -> employee.getSurname().equalsIgnoreCase(surname)).toList();
        } else if (name != null && !name.isEmpty() && !name.isBlank()) {
            employees = employees.stream()
                    .filter(employee -> employee.getName().equalsIgnoreCase(name)).toList();
        } else if (surname != null && !surname.isEmpty() && !surname.isBlank()) {
            employees = employees.stream()
                    .filter(employee -> employee.getSurname().equalsIgnoreCase(surname)).toList();
        }
        return employees.stream().map(employee -> {
                    EmployeeDto employeeDto = new EmployeeDto();
                    employeeDto.setName(employee.getName());
                    employeeDto.setSurname(employee.getSurname());
                    employeeDto.setRole(employee.getRole());
                    employeeDto.setHireDate(employee.getHireDate());
                    employeeDto.setSalary(employee.getSalary());
                    return employeeDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override //metodo per inserire un nuovo employee
    public Employee save(Employee employee) {

        em.persist(employee);
        return employee;
    }

    @Transactional
    @Override //metodo per aggiornare un employee
    public Employee update(Long id, EmployeeDto employeeDto) throws NotFoundException {

        // Trova l'employee con l'ID specificato
        Employee existingEmployee = findById(id);

        // Aggiorno le informazioni dell'employee esistente con quelle fornite nel parametro employeeDto
        existingEmployee.setName(employeeDto.getName());
        existingEmployee.setSurname(employeeDto.getSurname());
        existingEmployee.setHireDate(employeeDto.getHireDate());
        existingEmployee.setRole(employeeDto.getRole());
        existingEmployee.setSalary(employeeDto.getSalary());

        // aggiorno l'employee facendo il merge con il dato sul db
        em.merge(existingEmployee);

        return existingEmployee;
    }

    @Transactional
    @Override
    public void delete(Long id) throws NotFoundException {
        Employee employeeToDelete = findById(id);

        // Verifica se l'employee è associato a projects
        Set<Project> projects = employeeToDelete.getProjects();
        if (!projects.isEmpty()) {
            throw new NotFoundException("Employee with ID " + id + " is associated with projects. Remove employee from projects first.");
        }

        // Verifica se l'employee è associato a technologies
        Set<Technology> technologies = employeeToDelete.getTechnologies();
        if (!technologies.isEmpty()) {
            throw new NotFoundException("Employee with ID " + id + " is associated with technologies. Remove employee from technologies first.");
        }

        em.remove(employeeToDelete);
    }
}
