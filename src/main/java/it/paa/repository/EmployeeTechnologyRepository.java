package it.paa.repository;

public interface EmployeeTechnologyRepository {

    void addEmployeeToTechnology(Long technologyId, Long employeeId);
    void removeEmployeeFromTechnology(Long technologyId, Long employeeId);
}
