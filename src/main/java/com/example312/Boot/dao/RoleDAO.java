package com.example312.Boot.dao;

import com.example312.Boot.model.Role;

import java.util.List;

public interface RoleDAO {

    void addRole(Role role);
    List<Role> getAll();
    Role getRole(String name);
}
