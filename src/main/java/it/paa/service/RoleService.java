package it.paa.service;

import it.paa.model.Role;
import it.paa.repository.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;


import java.util.List;

@ApplicationScoped
public class RoleService implements RoleRepository {

    @Inject
    EntityManager entityManager;

    public List<Role> getAllRoles() {
        List<Role> roles = entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();
        return roles;
    }

    public Role getRoleById(Long id) {
        Role role = entityManager.find(Role.class, id);
        if (role == null) {
            throw new NotFoundException("Role with id " + id + " not found.");
        }
        return role;
    }

    @Transactional
    public Role createRole(Role role) {
        try {
            entityManager.persist(role);
            return role;
        } catch (PersistenceException e) {
            // Gestione dell'eccezione di violazione del vincolo di unicità
            throw new PersistenceException("Role with the same name already exists.");
        }
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = getRoleById(id);
        role.setName(roleDetails.getName());
        return entityManager.merge(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        try {
            Role role = getRoleById(id);
            entityManager.remove(role);
        } catch (NotFoundException e) {
            throw new NotFoundException("Role with id " + id + " not found.");
        }
    }
}
