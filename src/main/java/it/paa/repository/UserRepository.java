package it.paa.repository;

import it.paa.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByUsername(String username);

    void createUser(User user, Long idRole);

    void updateUser(User user, Long id);

    void deleteUser(Long id);
}
