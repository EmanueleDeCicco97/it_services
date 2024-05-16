package it.paa.controller;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import it.paa.model.User;
import it.paa.service.UserService;
import jakarta.annotation.security.PermitAll;
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
@Path("/users")
public class UserController {

    @Inject
    UserService userService;

    @POST
    @PermitAll
    @Path("/login")
    public Response login(@Valid User user) {
        try {
            User autenticateUser = userService.authenticateUser(user.getUsername(), user.getPassword());

            return Response.ok().entity(autenticateUser.getUsername() + " logged in successfully").type(MediaType.TEXT_PLAIN).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            User user = userService.getUserById(id);
            return Response.ok().entity(user).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }


    @POST
    public Response createUser(User user, @QueryParam("roleId") Long roleId) {
        try {
            userService.createUser(user, roleId);
            return Response.status(Response.Status.CREATED).type(MediaType.TEXT_PLAIN).entity("the user was created successfully").build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.CONFLICT).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        try {
            userService.updateUser(user, id);
            return Response.status(Response.Status.OK).type(MediaType.TEXT_PLAIN).entity("the user has been changed correctly").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT).entity("a user with the same username already exists").type(MediaType.TEXT_PLAIN).build();
        }

    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            userService.deleteUser(id);
            return Response.status(Response.Status.OK).type(MediaType.TEXT_PLAIN).entity("the user was successfully deleted").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT).entity("delete associations before deleting user").type(MediaType.TEXT_PLAIN).build();
        }

    }

    @PUT
    @Path("/{userId}/roles/{roleId}")
    public Response assignRoleToUser(@PathParam("userId") Long userId, @PathParam("roleId") Long roleId) {
        try {
            userService.assignRoleToUser(userId, roleId);
            return Response.status(Response.Status.OK).type(MediaType.TEXT_PLAIN).entity("the role was changed successfully").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @DELETE
    @Path("/{userId}/roles/{roleId}")
    public Response removeRoleFromUser(@PathParam("userId") Long userId, @PathParam("roleId") Long roleId) {
        try {
            userService.removeRoleFromUser(userId, roleId);
            return Response.status(Response.Status.OK).type(MediaType.TEXT_PLAIN).entity("the role was successfully revoked").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
