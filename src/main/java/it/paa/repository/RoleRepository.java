package it.paa.repository;

import it.paa.model.Role;

import java.util.List;

public interface RoleRepository {
    Role getRoleById(Long id);

    List<Role> getAllRoles();

    Role createRole(Role role);

    Role updateRole(Long id, Role role);

    void deleteRole(Long id);
}
