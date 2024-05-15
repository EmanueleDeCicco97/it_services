package it.paa.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import it.paa.model.Role;
import it.paa.model.User;
import it.paa.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    RoleService roleService;


    public User authenticateUser(String username, String password) {
        try {
            User user = getUserByUsername(username);

            if (BcryptUtil.matches(password, user.getPassword())) {
                System.out.println(user);
                return user;
            } else {
                return null;
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    public User getUserByUsername(String username) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("\n" + "Incorrect username or password ");
        }
    }

    public List<User> getAllUsers() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User getUserById(Long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    @Transactional
    public void createUser(User user, Long idRole) {
        try {
            Role role = roleService.getRoleById(idRole);
            String encryptedPassword = BcryptUtil.bcryptHash(user.getPassword());
            user.setRole(role);
            user.setPassword(encryptedPassword);
            entityManager.persist(user);
        } catch
        (PersistenceException e) {
            throw new PersistenceException("User already exists");
        }

    }

    @Transactional
    public void updateUser(User user, Long id) {
        User userCheck = entityManager.find(User.class, id);

        String encryptedPassword = BcryptUtil.bcryptHash(user.getPassword());
        userCheck.setPassword(encryptedPassword);

        entityManager.merge(userCheck);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("Role with id " + roleId + " does not exist");
        }
        user.setRole(role);
        updateUser(user, user.getId());
    }

    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist");
        }
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            throw new IllegalArgumentException("Role with id " + roleId + " does not exist");
        }
        if (user.getRole() != null && user.getRole().getId().equals(roleId)) {
            user.setRole(null);
            updateUser(user, user.getId());
        }
    }
}
