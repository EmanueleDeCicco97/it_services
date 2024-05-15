package it.paa.service;

import it.paa.model.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


import java.util.List;

@ApplicationScoped
public class RoleService {

    @Inject
    EntityManager entityManager;

    public List<Role> getAllRoles() {
        List<Role> roles = entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();
        return roles;
    }

    public Role getRoleById(Long id) {
        return entityManager.find(Role.class, id);
    }

    @Transactional
    public Role createRole(Role role) {
        entityManager.persist(role);
        return role;
    }

    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = entityManager.find(Role.class, id);
        if (role != null) {
            role.setName(roleDetails.getName());
            return entityManager.merge(role);
        } else {
            return null;
        }
    }

    @Transactional
    public boolean deleteRole(Long id) {
        Role role = getRoleById(id);
        if (role != null) {
            entityManager.remove(role);
            return true;
        } else {
            return false;
        }
    }
}
