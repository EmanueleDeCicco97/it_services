package it.paa.controller;


import it.paa.dto.ClientDto;
import it.paa.model.Client;
import it.paa.model.Employee;
import it.paa.service.ClientService;
import it.paa.service.EmployeeService;
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
    private ClientService clientService;

    @Inject
    EmployeeService employeeService;

    @Inject
    private Validator validator;

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
                    .entity("Client not found with id: " + id)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @POST //metodo per creare un nuovo cliente
    @Path("/employees/{employee_id}")
    public Response addClient(@PathParam("employee_id") Long employeeId, ClientDto clientDto) {
        try {
            // Controlla se l'ID del dipendente è stato fornito
            if (employeeId != null) {
                // Trova il dipendente corrispondente all'ID fornito
                Employee employee = employeeService.findById(employeeId);

                // Assegna il dipendente al cliente
                Client client = new Client();
                client.setContactPerson(employee);
                client.setName(clientDto.getName());
                client.setSector(clientDto.getSector());
                client.setAddress(clientDto.getAddress());

                Set<ConstraintViolation<ClientDto>> violations = validator.validate(clientDto);
                if (!violations.isEmpty()) {
                    String errorMessage = violations.stream()
                            .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                            .collect(Collectors.joining("\n"));
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(errorMessage)
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }

                // Effettua la validazione del cliente

                clientService.save(client); // Salva il cliente dopo la validazione
            }
            return Response.status(Response.Status.CREATED)
                    .entity("Client created successfully")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } catch (NotFoundException e) {
            // Gestione dell'eccezione se l'ID del dipendente non è valido
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }


    @PUT //metodo per aggiornare un cliente
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long id, ClientDto clientDto) {
        try {
            Set<ConstraintViolation<ClientDto>> violations = validator.validate(clientDto);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                        .collect(Collectors.joining("\n"));
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorMessage)
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            Client updatedClient = clientService.update(id, clientDto);
            return Response.ok(updatedClient).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @DELETE // metodo per eliminare un cliente
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        try {
            clientService.delete(id);
            return Response.ok()
                    .entity("Client successfully deleted")
                    .type(MediaType.TEXT_PLAIN)
                    .build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
