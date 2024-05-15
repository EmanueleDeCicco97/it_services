package it.paa.repository;

import it.paa.dto.EmployeeDto;
import it.paa.model.Employee;

import java.util.List;

public interface EmployeeRepository {

    Employee findById(Long id);

    Employee save(Employee employee);

    Employee update(Long id, EmployeeDto employeeDto);

    void delete(Long id);

    List<EmployeeDto> findAllByAttributes(String name, String surname);
}
