package it.paa.controller;

import it.paa.model.User;
import it.paa.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import javax.print.attribute.standard.Media;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
@Path("/users")
public class UserController {

    @Inject
    UserService userService;
    @Inject
    SecurityContext securityContext;

    @POST
    @PermitAll
    @Path("/login")
    public Response login(User user) {
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
    public User getUserById(@PathParam("id") Long id) {
        return userService.getUserById(id);
    }

    @POST
    public Response createUser(User user, @QueryParam("roleId") Long roleId) {
        try {
            userService.createUser(user, roleId);
            return Response.status(Response.Status.CREATED).entity("the user was created successfully").build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.CONFLICT).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {

        userService.updateUser(user, id);
        return Response.status(Response.Status.OK).type(MediaType.TEXT_PLAIN).entity("\n" + "the user has been changed correctly").build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.status(Response.Status.OK).type(MediaType.TEXT_PLAIN).entity("the user was successfully deleted").build();
    }


    @PUT
    @Path("/{userId}/roles/{roleId}")
    public Response assignRoleToUser(@PathParam("userId") Long userId, @PathParam("roleId") Long roleId) {
        userService.assignRoleToUser(userId, roleId);
        return Response.status(Response.Status.OK).type(MediaType.TEXT_PLAIN).entity("the role was changed successfully").build();
    }

    @DELETE
    @Path("/{userId}/roles/{roleId}")
    public Response removeRoleFromUser(@PathParam("userId") Long userId, @PathParam("roleId") Long roleId) {
        userService.removeRoleFromUser(userId, roleId);
        return Response.status(Response.Status.OK).type(MediaType.TEXT_PLAIN).entity("the role was successfully revoked").build();
    }
 //lalalala
}
