package it.paa.controller;


import it.paa.dto.ClientDto;
import it.paa.model.Client;
import it.paa.model.Employee;
import it.paa.service.ClientService;
import it.paa.service.EmployeeService;
import it.paa.util.ErrorMessage;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin")
@Path("/client")
public class ClientController {

    @Inject
    ClientService clientService;

    @Inject
    EmployeeService employeeService;

    @Inject
    Validator validator;

    @GET //metodo per recuperare tutti i clienti
    public Response getAllClients(@QueryParam("name") String name,
                                  @QueryParam("sector") String sector) {
        List<Client> clients = clientService.findAllByAttributes(name, sector);
        if (clients.isEmpty()) {
            return Response.noContent().build();
        }
        return Response.ok(clients).build();
    }

    @GET //metodo per recuperare un cliente in base all'ID
    @Path("/{id}")
    public Response getClientById(@PathParam("id") Long id) {
        try {
            Client client = clientService.findById(id);
            return Response.ok(client).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Client not found with id: " + id))
                    .build();
        }
    }

    @POST //metodo per creare un nuovo cliente
    public Response addClient(ClientDto clientDto) {
        try {

            Client client = new Client();

            Employee employee = null;

            if (clientDto.getEmployeeId() != null) {
                // Trova il dipendente corrispondente all'ID fornito e lo assegna al cliente
                employee = employeeService.findById(clientDto.getEmployeeId());

                // correzione assegnazione employee al client
                client.setContactPerson(employee);
            }
            client.setName(clientDto.getName());
            client.setSector(clientDto.getSector());
            client.setAddress(clientDto.getAddress());

            Set<ConstraintViolation<ClientDto>> violations = validator.validate(clientDto);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorMessage(errorMessage))
                        .build();
            }

            clientService.save(client); // Salva il cliente dopo la validazione

            return Response.status(Response.Status.CREATED)
                    .entity(new ErrorMessage("Client created successfully"))

                    .build();
        } catch (NotFoundException e) {
            // Gestione dell'eccezione se l'ID del dipendente non è valido
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }


    @PUT
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long id, ClientDto clientDto) {
        try {
            Set<ConstraintViolation<ClientDto>> violations = validator.validate(clientDto);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorMessage(errorMessage))
                        .build();
            }
            Client updatedClient = clientService.update(id, clientDto);
            return Response.ok(updatedClient).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }

    @DELETE // metodo per eliminare un cliente
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        try {
            clientService.delete(id);
            return Response.ok()
                    .entity(new ErrorMessage("Client successfully deleted"))
                    .build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage()))
                    .build();
        }
    }
}
