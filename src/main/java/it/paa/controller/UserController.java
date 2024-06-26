package it.paa.controller;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import it.paa.model.User;
import it.paa.service.UserService;
import it.paa.util.ErrorMessage;
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

            return Response.ok().entity(new ErrorMessage(autenticateUser.getUsername() + " logged in successfully")).build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorMessage(e.getMessage()))
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
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }


    @POST
    @Path("/{roleId}")
    public Response createUser(User user, @PathParam("roleId") Long roleId) {
        try {
            // Controllo se esiste già un utente con lo stesso username (ignorando il case)
            User existingUser = userService.getUserByUsernameIgnoreCase(user.getUsername());
            if (existingUser != null) {
                throw new PersistenceException("User already exists");
            }

            userService.createUser(user, roleId);
            return Response.status(Response.Status.CREATED).entity(new ErrorMessage("the user was created successfully")).build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorMessage(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        try {
            // Controllo se esiste già un utente con lo stesso username (ignorando il case) diverso dall'utente che si sta aggiornando
            User existingUser = userService.getUserByUsernameIgnoreCase(user.getUsername());
            if (existingUser != null && !existingUser.getId().equals(id)) {
                throw new PersistenceException("User with the same username already exists");
            }
            userService.updateUser(user, id);
            return Response.status(Response.Status.OK).entity(new ErrorMessage("the user has been changed correctly")).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorMessage("a user with the same username already exists")).build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorMessage(e.getMessage())).build();
        }

    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            userService.deleteUser(id);
            return Response.status(Response.Status.OK).entity(new ErrorMessage("the user was successfully deleted")).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorMessage("delete associations before deleting user")).build();
        }

    }

    @PUT
    @Path("/assignRole/{userId}/roles/{roleId}")
    public Response assignRoleToUser(@PathParam("userId") Long userId, @PathParam("roleId") Long roleId) {
        try {
            userService.assignRoleToUser(userId, roleId);
            return Response.status(Response.Status.OK).entity(new ErrorMessage("the role was changed successfully")).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/revokeRole/{userId}")
    public Response revokeRoleFromUser(@PathParam("userId") Long userId) {
        try {
            userService.removeRoleFromUser(userId);
            return Response.status(Response.Status.OK).entity(new ErrorMessage("the role was successfully revoked")).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }
}
