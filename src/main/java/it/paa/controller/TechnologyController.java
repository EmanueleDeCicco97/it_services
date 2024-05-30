package it.paa.controller;


import io.quarkus.arc.ArcUndeclaredThrowableException;
import it.paa.dto.TechnologyDto;
import it.paa.model.Project;
import it.paa.model.Technology;
import it.paa.service.TechnologyService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
@Path("/technology")
public class TechnologyController {
    @Inject
    TechnologyService technologyService;
    @Inject
    Validator validator;


    @GET //metodo per recuperare tutte le tecnologie in base ai criteri forniti
    public Response getAllTechnologies(@QueryParam("name") String name,
                                       @QueryParam("experienceLevel") String experienceLevel) {
        List<Technology> technologies = technologyService.findAllByAttributes(name, experienceLevel);
        if (technologies.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(technologies).build();
    }

    @GET
    @Path("/{id}") //metodo per recuperare una tecnologia in base all'id
    public Response getTechnologyById(@PathParam("id") Long id) {
        try {
            Technology technology = technologyService.findById(id);

            return Response.ok(technology).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @POST //metodo per salvare una tecnologia
    public Response addTechnology(TechnologyDto technologyDto) {
        try {

            // validazione dell'entità Project
            Set<ConstraintViolation<TechnologyDto>> violations = validator.validate(technologyDto);

            if (!violations.isEmpty()) {
                // Gestione degli errori di validazione
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));

                return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).type(MediaType.TEXT_PLAIN).build();
            }
            // Controllo se esiste già una tecnologia con lo stesso nome (ignorando il case)
            Technology existingTechnology = technologyService.getTechnologyByNameIgnoreCase(technologyDto.getName());
            if (existingTechnology != null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("A technology with the same name already exists").type(MediaType.TEXT_PLAIN).build();
            }

            Technology technology = new Technology();
            technology.setName(technologyDto.getName());
            technology.setRequiredExperienceLevel(technologyDto.getRequiredExperienceLevel());
            technology.setDescription(technologyDto.getDescription());


            technologyService.save(technology);

            return Response.status(Response.Status.CREATED).type(MediaType.TEXT_PLAIN).entity("Technology created successfully").build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("a technology with the same name already exists").type(MediaType.TEXT_PLAIN).build();
        }
    }

    @PUT
    @Path("/{id}") //metodo per aggiornare una tecnologia
    public Response updateTechnology(@PathParam("id") Long id, TechnologyDto technologyDto) {

        try {

            // Controllo se esiste già una tecnologia con lo stesso nome (ignorando il case)
            Technology existingTechnologyDB = technologyService.getTechnologyByNameIgnoreCase(technologyDto.getName());
            if (existingTechnologyDB != null && !existingTechnologyDB.getId().equals(id)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("A technology with the same name already exists").type(MediaType.TEXT_PLAIN).build();
            }
            Technology existingTechnology = technologyService.findById(id);

            // Aggiorno le informazioni della tecnologia esistente con quelle fornite nel DTO
            existingTechnology.setName(technologyDto.getName());
            existingTechnology.setRequiredExperienceLevel(technologyDto.getRequiredExperienceLevel());
            existingTechnology.setDescription(technologyDto.getDescription());

            // Validazione dell'entità Project
            Set<ConstraintViolation<TechnologyDto>> violations = validator.validate(technologyDto);

            if (!violations.isEmpty()) {
                // Gestione degli errori di validazione
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));

                return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).type(MediaType.TEXT_PLAIN).build();
            }
            Technology updatedTechnology = technologyService.update(existingTechnology);
            return Response.ok(updatedTechnology).build();

        } catch (NotFoundException e) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("a technology with the same name already exists").type(MediaType.TEXT_PLAIN).build();
        }
    }

    @DELETE //metodo per cancellare una tecnologia
    @Path("/{id}")
    public Response deleteTechnology(@PathParam("id") Long id) {
        try {
            technologyService.delete(id);
            return Response.ok().entity("Technology deleted successfully").type(MediaType.TEXT_PLAIN).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (ArcUndeclaredThrowableException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("remove associations before removing a technology")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    //    //•	Esercitazione 2: Creare un endpoint per trovare le tecnologie più richieste dai clienti e visualizzare i dettagli
//    // dei progetti in cui sono utilizzate queste tecnologie.
//    @GET
//    @RolesAllowed({"admin", "project manager"})
//    // ritengo opportuno come autorizzazione avanzata che sia l'admin che il pm possono vedere l'andamento delle varie tecnologie
    @GET
    @Path("/most-technologies")
    public Response getMostCommonTechnologies() {
        try {

            Map<Technology, Set<Project>> mostCommonTechnology = technologyService.findMostTechnologies();

            return Response.ok(mostCommonTechnology).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NO_CONTENT).type(MediaType.TEXT_PLAIN).entity("No technology found").build();
        }
    }
}

