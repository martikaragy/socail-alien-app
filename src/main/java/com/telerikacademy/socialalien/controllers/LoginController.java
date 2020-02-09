package com.telerikacademy.socialalien.controllers;

import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLogInPage() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String showAccessDeniedPage() {
        return "access-denied";
    }

    @PostMapping("/login/search")
    public String process(Model model, @RequestParam String sourceText) {
        model.addAttribute("users",userService.getUsersBySubstring(sourceText));
        return "login";
    }

}
