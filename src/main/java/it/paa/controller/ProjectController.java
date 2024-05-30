package it.paa.controller;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import it.paa.dto.ProjectDto;
import it.paa.model.Project;
import it.paa.model.Technology;
import it.paa.model.User;
import it.paa.service.ProjectService;
import it.paa.service.UserService;
import it.paa.util.ErrorMessage;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Invalid date format. Make sure you use the format dd-MM-yyyy."))
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
                        .entity(new ErrorMessage("You are not authorized to update this project"))
                        .build();
            }

            return Response.ok(project).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
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
            return Response.noContent().entity(new ErrorMessage("no projects found")).build();
        }
        return Response.ok(map).build();
    }

    @POST //metodo per aggiungere un progetto
    @Path("/{userId}")
    public Response addProject(@PathParam("userId") Long userId, ProjectDto projectDto) {
        try {
            // validazione dell'entità Project
            Set<ConstraintViolation<ProjectDto>> violations = validator.validate(projectDto);
            if (!violations.isEmpty()) {
                // gestione degli errori di validazione
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));

                return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(errorMessage)).build();
            }

            // Controllo se esiste già un progetto con lo stesso nome (ignorando il case)
            User user = userService.getUserById(userId);

            // creo un nuovo progetto con i dati del dto
            Project project = new Project();
            project.setName(projectDto.getName());
            project.setDescription(projectDto.getDescription());
            project.setStartDate(projectDto.getStartDate());
            project.setEndDate(projectDto.getEndDate());
            project.setUser(user);

            Project existingProject = projectService.getProjectByNameIgnoreCase(projectDto.getName());
            if (existingProject != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage("A project with the same name already exists")).build();
            }

            projectService.save(project);
            return Response.status(Response.Status.CREATED).entity(new ErrorMessage("Project created successfully")).build();
        } catch (NotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage(e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage("a project with the same name already exists")).build();
        }
    }

    @RolesAllowed({"admin", "project manager"})
    @PUT // metodo per aggiornare un progetto
    @Path("/{id}")
    public Response updateProject(@PathParam("id") Long id, ProjectDto projectDto) {
        try {
            // Ottengo l'utente corrente dal SecurityContext
            String currentUsername = securityContext.getUserPrincipal().getName();

            // Verifico se l'utente corrente è il project manager del progetto
            Project project = projectService.findById(id);
            if (!project.getUser().getUsername().equals(currentUsername) && securityContext.isUserInRole("project manager")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorMessage("You are not authorized to update this project"))
                        .build();
            }

            // validazione dell'entità Project
            Set<ConstraintViolation<ProjectDto>> violations = validator.validate(projectDto);
            if (!violations.isEmpty()) {
                // gestione degli errori di validazione
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));

                return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage(errorMessage)).build();
            }

            // Controllo se esiste già un progetto con lo stesso nome (ignorando il case) diverso dall'attuale progetto
            Project existingProject = projectService.getProjectByNameIgnoreCase(projectDto.getName());
            if (existingProject != null && !existingProject.getId().equals(id)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage("A project with the same name already exists")).build();
            }

            Project updatedProject = projectService.update(id, projectDto);
            return Response.ok(updatedProject).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorMessage("An error occurred while updating the project")).build();
        }
    }


    @RolesAllowed({"admin", "project manager"})
    @DELETE //metodo per cancellare un progetto
    @Path("/{id}")
    public Response deleteProject(@PathParam("id") Long id) {
        try {
            // Ottengo l'utente corrente dal SecurityContext
            String currentUsername = securityContext.getUserPrincipal().getName();
            // Verifico se l'utente corrente è il project manager del progetto o l'admin
            Project project = projectService.findById(id);
            if (!project.getUser().getUsername().equals(currentUsername) && securityContext.isUserInRole("project manager")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorMessage("You are not authorized to update this project"))
                        .build();
            }
            projectService.delete(id);
            return Response.ok().entity(new ErrorMessage("Project successfully deleted")).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorMessage("remove associations before removing a project"))
                    .build();
        }
    }
}
