package it.paa.controller;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import it.paa.dto.ProjectDto;
import it.paa.model.Project;
import it.paa.model.Technology;
import it.paa.model.User;
import it.paa.service.ProjectService;
import it.paa.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("project manager")
@Path("/project")
public class ProjectController {

    @Inject
    ProjectService projectService;

    @Inject
    Validator validator;

    @Inject
    UserService userService;

    @Inject
    SecurityContext securityContext;

    @RolesAllowed({"admin", "project manager"})
    @GET //metodo per recuperare tutti i progetti in base al nome e alla data di inizio
    public Response getAllProjects(@QueryParam("name") String name,
                                   @QueryParam("startDate") String startDateStr) {

        LocalDate startDate = null;
        try {

            if (startDateStr != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                // Effettuo il parsing della stringa utilizzando il formato definito
                startDate = LocalDate.parse(startDateStr, formatter);
            }

        } catch (DateTimeParseException e) {
            // gestisco eventuali errori nel parsing
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
                    .entity("\n" + "Invalid date format. Make sure you use the format dd-MM-yyyy.")
                    .build();
        }

        List<Project> projects = projectService.findAllByAttributes(name, startDate);


        if (securityContext.isUserInRole("project manager")) {
            projects = projects.stream().filter(project -> project.getUser().getUsername().equals(securityContext.getUserPrincipal().getName())).toList();
        }

        if (projects.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(projects).build();
    }

    @RolesAllowed({"admin", "project manager"})
    @GET //metodo per recuperare tutti i progetti in base all'id
    @Path("/{id}")
    public Response getProjectById(@PathParam("id") Long id) {
        try {
            Project project = projectService.findById(id);
            String currentUsername = securityContext.getUserPrincipal().getName();
            if (!project.getUser().getUsername().equals(currentUsername) && securityContext.isUserInRole("project manager")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You are not authorized to update this project")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }

            return Response.ok(project).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    //•	API pubblica di visualizzazione dei progetti con dettaglio delle tecnologie
    @GET
    @PermitAll
    @Path("withTechnologies")
    public Response getProjectsWithTechnologies() {

        Map<Project, Set<Technology>> map = projectService.getProjectsWithTechnologies();

        if (map.isEmpty()) {
            return Response.noContent().type(MediaType.TEXT_PLAIN).entity("no projects found").build();
        }
        return Response.ok(map).build();
    }

    @POST //metodo per aggiungere un progetto
    public Response addProject(@QueryParam("userId") Long userId, ProjectDto projectDto) {
        try {
            // validazione dell'entità Project
            Set<ConstraintViolation<ProjectDto>> violations = validator.validate(projectDto);
            if (!violations.isEmpty()) {
                // gestione degli errori di validazione
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));

                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(errorMessage).build();
            }

            User user = userService.getUserById(userId);
            // creo un nuovo progetto con i dati del dto
            Project project = new Project();
            project.setName(projectDto.getName());
            project.setDescription(projectDto.getDescription());
            project.setStartDate(projectDto.getStartDate());
            project.setEndDate(projectDto.getEndDate());
            project.setUser(user);

            projectService.save(project);
            return Response.status(Response.Status.CREATED).type(MediaType.TEXT_PLAIN).entity("Project created successfully").build();
        } catch (NotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }
    }

    @RolesAllowed({"admin", "project manager"})
    @PUT //metodo per aggiornare un progetto
    @Path("/{id}")
    public Response updateProject(@PathParam("id") Long id, ProjectDto projectDto) {
        try {
            // Ottenere l'utente corrente dal SecurityContext
            String currentUsername = securityContext.getUserPrincipal().getName();

            // Verificare se l'utente corrente è il project manager del progetto o l'admin
            Project project = projectService.findById(id);
            if (!project.getUser().getUsername().equals(currentUsername) && securityContext.isUserInRole("project manager")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You are not authorized to update this project")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }

            // validazione dell'entità Project
            Set<ConstraintViolation<ProjectDto>> violations = validator.validate(projectDto);
            if (!violations.isEmpty()) {
                // gestione degli errori di validazione
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));

                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(errorMessage).build();
            }

            Project updatedProject = projectService.update(id, projectDto);
            return Response.ok(updatedProject).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @RolesAllowed({"admin", "project manager"})
    @DELETE //metodo per cancellare un progetto
    @Path("/{id}")
    public Response deleteProject(@PathParam("id") Long id) {
        try {
            // Ottenere l'utente corrente dal SecurityContext
            String currentUsername = securityContext.getUserPrincipal().getName();
            // Verificare se l'utente corrente è il project manager del progetto o l'admin
            Project project = projectService.findById(id);
            if (!project.getUser().getUsername().equals(currentUsername) && securityContext.isUserInRole("project manager")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You are not authorized to update this project")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            projectService.delete(id);
            return Response.ok().entity("Project successfully deleted").type(MediaType.TEXT_PLAIN).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("remove associations before removing a project")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @RolesAllowed({"admin", "project manager"})
    @POST    //Aggiungo un dipendente ad un progetto
    @Path("{idProject}/employee/{idEmployee}")
    public Response addEmployeeToProject(@PathParam("idProject") Long idProject,
                                         @PathParam("idEmployee") Long idEmployee) {

        try {
            // Ottenere l'utente corrente dal SecurityContext
            String currentUsername = securityContext.getUserPrincipal().getName();
            // Verificare se l'utente corrente è il project manager del progetto o l'admin
            Project project = projectService.findById(idProject);
            if (!project.getUser().getUsername().equals(currentUsername) && securityContext.isUserInRole("project manager")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You are not authorized to update this project")
                        .type(MediaType.TEXT_PLAIN)
                        .build();

            }
            projectService.addEmployeeToProject(idProject, idEmployee);

            return Response.status(Response.Status.CREATED).entity("project correctly assigned to the employee").type(MediaType.TEXT_PLAIN).build();
        } catch (NotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (EntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }
    }
}
