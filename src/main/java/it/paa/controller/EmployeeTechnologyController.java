package it.paa.controller;

import it.paa.service.EmployeeTechnologyService;
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
            @PathParam("employeeId") Long employeeId
    ) {
        try {

            employeeTechnologyService.addEmployeeToTechnology(technologyId, employeeId);
            return Response.ok().entity("Employee successfully added to the technology.").type(MediaType.TEXT_PLAIN).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();

        } catch (EntityExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        } catch (IllegalArgumentException e) {

            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }
    }
}
