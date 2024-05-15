package it.paa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.paa.validation.ValidSalary;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

//Validazioni avanzate (facoltative)
//•	Dipendente: Assicurarsi che lo stipendio sia congruo rispetto al ruolo e all'esperienza del dipendente.

@ValidSalary // validatore per lo stipendio rispetto al ruolo del dipendente'
public class EmployeeDto {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Surname cannot be empty")
    private String surname;

    @NotBlank(message = "Role cannot be empty")
    private String role;

    @JsonFormat(pattern = "dd-MM-yyyy") //validatore per formato data
    private LocalDate hireDate;

    private double salary;

    public EmployeeDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "EmployeeDto{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", role='" + role + '\'' +
                ", hireDate=" + hireDate +
                ", salary=" + salary +
                '}';
    }
}
