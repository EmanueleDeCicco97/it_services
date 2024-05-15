package it.paa.repository;

import it.paa.dto.ClientDto;
import it.paa.model.Client;

import java.util.List;

public interface ClientRepository {
    Client findById(Long id);

    Client save(Client client);

    Client update(Long id, ClientDto clientDto);

    void delete(Long id);

    List<Client> findAllByAttributes(String name, String sector);
}
