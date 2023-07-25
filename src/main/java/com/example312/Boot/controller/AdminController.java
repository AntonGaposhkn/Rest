package com.example312.Boot.controller;

import com.example312.Boot.model.User;
import com.example312.Boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//Сюда заходит только админ
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getInfo(@AuthenticationPrincipal User admin, Model model) {
        model.addAttribute("user", admin);
        model.addAttribute("adminActive", "active");
        return "users/showUser";
    }

    @GetMapping("/users")
    public String getAllUsers() {
        return "users/all";
    }
}
