package it.paa.repository;

import it.paa.model.User;

import java.util.List;

public interface UserRepository {

    User authenticateUser(String username, String password);

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByUsername(String username);

    void createUser(User user, Long idRole);

    void updateUser(User user, Long id);

    void deleteUser(Long id);

    void assignRoleToUser(Long userId, Long roleId);

    void removeRoleFromUser(Long userId);

    User getUserByUsernameIgnoreCase(String username);
}
