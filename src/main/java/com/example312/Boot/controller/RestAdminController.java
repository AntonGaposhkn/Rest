package com.example312.Boot.controller;

import com.example312.Boot.dao.RoleDAO;
import com.example312.Boot.model.Role;
import com.example312.Boot.model.User;
import com.example312.Boot.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/api/admin")
public class RestAdminController {

    private final UserService userService;
    private final RoleDAO roleDAO;

    public RestAdminController(UserService userService, RoleDAO roleDAO) {
        this.userService = userService;
        this.roleDAO = roleDAO;
    }

    @GetMapping
    public ResponseEntity<User> getUser(@AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/roles")
    public ResponseEntity<Set<Role>> getAdminRoles(@AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(admin.getRoles());
    }

    @GetMapping("/roles/all")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleDAO.getAll());
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody String json) throws JsonProcessingException {
        User user = getUserFromJson(json);
        userService.addUser(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/users")
    public ResponseEntity<HttpStatus> update(@RequestBody String json) {
        User user = getUserWithIdFromJson(json);
        userService.updateUser(user.getId(), user); //Находим по id того юзера, которого надо изменить
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private User getUserWithIdFromJson(String json) {
        User user = getUserFromJson(json);
        user.setId(Long.valueOf(JsonPath.parse(json).read("$.id")));
        return user;
    }

    private User getUserFromJson(String json) {
        User user = new User();
        user.setName(JsonPath.parse(json).read("$.name"));
        user.setSurname(JsonPath.parse(json).read("$.surname"));
        user.setEmail(JsonPath.parse(json).read("$.email"));
        user.setPassword(JsonPath.parse(json).read("$.password"));
        List<Map<String, String>> roles = JsonPath.parse(json).read("$.roles");
        user.setRoles(Set.of(roleDAO.getRole(roles.get(0).get("name"))));
        return user;
    }
}
