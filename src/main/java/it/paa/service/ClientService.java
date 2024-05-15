package it.paa.service;

import it.paa.dto.ClientDto;
import it.paa.model.Client;
import it.paa.repository.ClientRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClientService implements ClientRepository {

    @PersistenceContext
    private EntityManager em;

    @Override // metodo per restituire il cliente in base al suo id
    public Client findById(Long id) throws NotFoundException {
        Client client = em.find(Client.class, id);
        if (client == null) {
            throw new NotFoundException("Client not found with id: " + id);
        }
        return client;
    }

    @Override//metodo per restituire tutti i clienti in base ai parametri passati
    public List<Client> findAllByAttributes(String name, String sector) {
        List<Client> filteredClients = em.createQuery("from Client", Client.class).getResultList();
        return filteredClients.stream()
                .filter(client -> client.getName().equalsIgnoreCase(name))
                .filter(client -> client.getSector().equalsIgnoreCase(sector))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override //metodo per inserire un nuovo cliente
    public Client save(Client client) {
        em.persist(client);
        return client;
    }

    @Transactional
    @Override//metodo per aggiornare un cliente
    public Client update(Long id, ClientDto clientDto) throws NotFoundException {
        Client existingClient = findById(id);

        existingClient.setName(clientDto.getName());
        existingClient.setSector(clientDto.getSector());
        existingClient.setAddress(clientDto.getAddress());
        em.merge(existingClient);
        return existingClient;
    }

    @Transactional
    @Override //metodo per eliminare un cliente
    public void delete(Long id) throws NotFoundException {
        Client clientToDelete = findById(id);

        em.remove(clientToDelete);
    }

    public List<Client> findAll() {
        List<Client> clients = em.createQuery("from Client", Client.class).getResultList();
        return clients;
    }
}
