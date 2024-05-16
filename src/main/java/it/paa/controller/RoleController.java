package it.paa.controller;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import it.paa.model.Role;
import it.paa.service.RoleService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
@Path("/roles")
public class RoleController {

    @Inject
    RoleService roleService;

    @GET
    public Response getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return Response.ok(roles).build();
    }

    @GET
    @Path("/{id}")
    public Response getRoleById(@PathParam("id") Long id) {
        try {
            Role role = roleService.getRoleById(id);
            return Response.ok(role).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }


    @POST
    public Response createRole(@Valid Role role) {
        try {
            // Controllo se esiste già un ruolo con lo stesso nome (ignorando il case)
            Role existingRole = roleService.getRoleByNameIgnoreCase(role.getName());
            if (existingRole != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("A role with the same name already exists")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            Role createdRole = roleService.createRole(role);
            return Response.status(Response.Status.CREATED).entity(createdRole).build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }

    }

    @PUT
    @Path("/{id}")
    public Response updateRole(@PathParam("id") Long id, @Valid Role roleDetails) {
        try {
            // Controllo se esiste già un ruolo con lo stesso nome (ignorando il case) diverso dall'attuale ruolo
            Role existingRole = roleService.getRoleByNameIgnoreCase(roleDetails.getName());
            if (existingRole != null && !existingRole.getId().equals(id)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("A role with the same name already exists")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }


            Role updatedRole = roleService.updateRole(id, roleDetails);
            return Response.ok(updatedRole).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT).entity("A role with the name entered already exists").type(MediaType.TEXT_PLAIN).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRole(@PathParam("id") Long id) {
        try {
            roleService.deleteRole(id);
            return Response.ok().build();

        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT).entity("remove associated users before deleting the role").type(MediaType.TEXT_PLAIN).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
