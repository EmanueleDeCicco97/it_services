package it.paa.controller;

import it.paa.dto.EmployeeDto;
import it.paa.model.Employee;
import it.paa.service.EmployeeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
@Path("/employee")
public class EmployeeController {

    @Inject
     EmployeeService employeeService;
    @Inject
     Validator validator;

    //•	Esercitazione 1: Realizzare una funzionalità per ottenere tutti i progetti in cui è coinvolto
    // un dipendente specifico e visualizzare i dettagli dei progetti e dei clienti coinvolti
    @GET //metodo per recuperare tutti gli employee con i parametri di ricerca
    public Response getAllEmployees(@QueryParam("name") String name,
                                    @QueryParam("surname") String surname) {
        List<EmployeeDto> employees = employeeService.findAllByAttributes(name, surname);
        if (employees.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(employees).build();
    }


    // nella get by id c'è la funzionalità di recuperare anche project e client per l'esercitazione avanzata esercizio 1
    @GET
    @RolesAllowed({"admin","project manager"}) //puo essere utilizzato sia da admin che da project manager
    @Path("/{id}")
    public Response getEmployeeById(@PathParam("id") Long id) {
        try {
            Employee employee = employeeService.findById(id);
            return Response.ok(employee).build();
        } catch (NotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND).type(MediaType.TEXT_PLAIN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST //metodo per aggiungere un employee
    @Produces(MediaType.TEXT_PLAIN)
    public Response addEmployee(EmployeeDto employeeDto) {

        // Validazione dell'entità Project
        Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employeeDto);

        if (!violations.isEmpty()) {
            // Gestione degli errori di validazione
            String errorMessage = violations.stream()
                    .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                    .collect(Collectors.joining("\n"));

            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();

        }

        Employee employee = new Employee();
        employee.setName(employeeDto.getName());
        employee.setSurname(employeeDto.getSurname());
        employee.setRole(employeeDto.getRole());
        employee.setHireDate(employeeDto.getHireDate());
        employee.setSalary(employeeDto.getSalary());

        employeeService.save(employee);
        return Response.status(Response.Status.CREATED).entity("\n" + "Employee created successfully").build();
    }

    @PUT //metodo per aggiornare un employee
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Response updateEmployee(@PathParam("id") Long id, EmployeeDto employeeDto) {

        try {
            // Validazione dell'entità Project
            Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(employeeDto);

            if (!violations.isEmpty()) {
                // Gestione degli errori di validazione
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));

                return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();

            }

            Employee updatedEmployee = employeeService.update(id, employeeDto);
            return Response.ok(updatedEmployee).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @DELETE //metodo per eliminare un employee
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Response deleteEmployee(@PathParam("id") Long id) {
        try {
            employeeService.delete(id);
            return Response.ok().entity("Employee successfully eliminated").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
