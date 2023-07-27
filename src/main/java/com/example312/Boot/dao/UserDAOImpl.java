package com.example312.Boot.dao;


import com.example312.Boot.model.Role;
import com.example312.Boot.model.User;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserDAOImpl implements UserDAO {


    @PersistenceContext
    private EntityManager entityManager;

    private final RoleDAO roleDAO;

    public UserDAOImpl(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    @Override
    public User getUserById(long id) {
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        query.setParameter("id", id);
        return query.getSingleResult();

    }

    @Override
    @Transactional
    public User getUserByName(String name) {
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.name = :name", User.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public User getUserByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean addUser(User user) {
        User userFromDb = getUserByEmail(user.getEmail());
        if (userFromDb != null) {
            return false;
        }
        setRolesFromDb(user);
        entityManager.persist(user);
        return true;
    }

    @Override
    public void delete(long id) {
        User user = entityManager.find(User.class, id);
        entityManager.remove(user);
    }

    @Override
    public void updateUser(long id, User updatedUser) {
        User user = entityManager.find(User.class, id);
        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());
        user.setEmail(updatedUser.getEmail());
        user.setAge(updatedUser.getAge());
        user.setPassword(updatedUser.getPassword());
        setRolesFromDb(user);
        entityManager.persist(user);
    }
    private Role getRole(String name) {
        TypedQuery<Role> query = entityManager.createQuery("select r from Role r where r.name = :name", Role.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    private void setRolesFromDb(User user) {
        Set<Role> roles = user.getRoles();
        Set<Role> rolesFromDb = new HashSet<>();
        for (Role role : roles) {
            rolesFromDb.add(roleDAO.getRole(role.getName()));
        }
        user.setRoles(rolesFromDb);
    }
}
