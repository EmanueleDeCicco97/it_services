package it.paa.repository;

import it.paa.model.Role;
import java.util.List;

public interface RoleRepository{
    Role findById(Long id);
    List<Role> findAll();
    void persist(Role role);
    Role update(Role role);
    void deleteById(Long id);
}
