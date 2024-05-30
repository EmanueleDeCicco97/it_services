package it.paa.controller;

import it.paa.service.EmployeeTechnologyService;
import it.paa.util.ErrorMessage;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "project manager"})
@Path("/employeeTechnology")
public class EmployeeTechnologyController {

    @Inject
    EmployeeTechnologyService employeeTechnologyService;


    @POST //metodo per associare un employee a una tecnologia
    @Path("/{technologyId}/employee/{employeeId}")
    public Response addEmployeeToTechnology(
            @PathParam("technologyId") Long technologyId,
            @PathParam("employeeId") Long employeeId) {
        try {

            employeeTechnologyService.addEmployeeToTechnology(technologyId, employeeId);
            return Response.ok().entity(new ErrorMessage("Employee successfully added to the technology.")).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage(e.getMessage())).build();

        } catch (EntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorMessage(e.getMessage())).build();
        }
    }

    @DELETE // Metodo per rimuovere un employee da una tecnologia
    @Path("/{technologyId}/employee/{employeeId}")
    public Response removeEmployeeFromTechnology(
            @PathParam("technologyId") Long technologyId,
            @PathParam("employeeId") Long employeeId
    ) {
        try {
            employeeTechnologyService.removeEmployeeFromTechnology(technologyId, employeeId);
            return Response.ok().entity(new ErrorMessage("Employee successfully removed from the technology.")).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorMessage(e.getMessage())).build();

        }
    }

}
