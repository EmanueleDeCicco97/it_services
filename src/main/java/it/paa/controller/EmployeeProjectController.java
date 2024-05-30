package it.paa.controller;

import it.paa.model.Project;
import it.paa.service.EmployeeProjectService;
import it.paa.service.ProjectService;
import it.paa.util.ErrorMessage;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "project manager"})
@Path("/employeeProject")
public class EmployeeProjectController {

    @Inject
    EmployeeProjectService employeeProjectService;

    @Inject
    ProjectService projectService;

    @Inject
    SecurityContext securityContext;

    @POST    //Aggiungo un dipendente ad un progetto
    @Path("{idProject}/employee/{idEmployee}")
    public Response addEmployeeToProject(@PathParam("idProject") Long idProject,
                                         @PathParam("idEmployee") Long idEmployee) {

        try {
            // Ottenere l'utente corrente dal SecurityContext
            String currentUsername = securityContext.getUserPrincipal().getName();
            // Verifico se l'utente corrente è il project manager
            Project project = projectService.findById(idProject);
            if (!project.getUser().getUsername().equals(currentUsername) && securityContext.isUserInRole("project manager")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorMessage("You are not authorized to update this project"))
                        .build();

            }
            employeeProjectService.addEmployeeToProject(idProject, idEmployee);

            return Response.status(Response.Status.CREATED).entity(new ErrorMessage("project correctly assigned to the employee")).build();
        } catch (NotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        } catch (EntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorMessage(e.getMessage())).build();
        }
    }

    @DELETE // Metodo per dissociare un dipendente da un progetto
    @Path("{idProject}/employee/{idEmployee}")
    public Response removeEmployeeFromProject(@PathParam("idProject") Long idProject,
                                              @PathParam("idEmployee") Long idEmployee) {
        try {
            // Ottenere l'utente corrente dal SecurityContext
            String currentUsername = securityContext.getUserPrincipal().getName();
            // Verifico se l'utente corrente è il project manager
            Project project = projectService.findById(idProject);
            if (!project.getUser().getUsername().equals(currentUsername) && securityContext.isUserInRole("project manager")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorMessage("You are not authorized to update this project"))
                        .build();
            }
            employeeProjectService.removeEmployeeFromProject(idProject, idEmployee);

            return Response.ok().entity(new ErrorMessage("Employee removed from the project.")).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }
}
