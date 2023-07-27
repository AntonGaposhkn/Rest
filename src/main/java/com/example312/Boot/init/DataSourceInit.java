package com.example312.Boot.init;

import com.example312.Boot.dao.RoleDAO;
import com.example312.Boot.dao.UserDAO;
import com.example312.Boot.model.Role;
import com.example312.Boot.model.User;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class DataSourceInit {

    private final RoleDAO roleDAO;
    private final UserDAO userDAO;

    public DataSourceInit(RoleDAO roleDAO, UserDAO userDAO) {
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        Role roleAdmin = new Role("ROLE_ADMIN");
        roleDAO.addRole(roleAdmin);
        Role roleUser = new Role("ROLE_USER");
        roleDAO.addRole(roleUser);
        User user = new User("admin", "admin", "admin@a.ru", "123", (byte) 30, Set.of(roleAdmin, roleUser));
        userDAO.addUser(user);
        user = new User("user", "user", "user@a.ru", "456", (byte) 25, Set.of(roleUser));
        userDAO.addUser(user);
    }
}
