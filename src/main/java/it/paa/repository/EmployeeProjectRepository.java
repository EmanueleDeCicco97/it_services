package it.paa.repository;

public interface EmployeeProjectRepository {
    
    void addEmployeeToProject(Long projectId, Long employeeId);

    void removeEmployeeFromProject(Long projectId, Long employeeId);
}
