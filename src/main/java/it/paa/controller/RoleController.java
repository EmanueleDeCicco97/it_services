package it.paa.controller;

import it.paa.model.Role;
import it.paa.service.RoleService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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
        Role role = roleService.getRoleById(id);
        if (role != null) {
            return Response.ok(role).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response createRole(@Valid Role role) {
        Role createdRole = roleService.createRole(role);
        return Response.status(Response.Status.CREATED).entity(createdRole).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateRole(@PathParam("id") Long id, @Valid Role roleDetails) {
        Role updatedRole = roleService.updateRole(id, roleDetails);
        if (updatedRole != null) {
            return Response.ok(updatedRole).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRole(@PathParam("id") Long id) {
        boolean deleted = roleService.deleteRole(id);
        if (deleted) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
