package com.telerikacademy.socialalien.controllers;

import com.telerikacademy.socialalien.exceptions.DuplicateEntityException;
import com.telerikacademy.socialalien.exceptions.EntityNotFoundException;
import com.telerikacademy.socialalien.exceptions.InvalidInputException;
import com.telerikacademy.socialalien.models.dtos.UserDto;
import com.telerikacademy.socialalien.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
public class RegistrationController {
    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService=userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("exceptionMessage", "");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid UserDto userDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return "register";
        }
        try {
            userService.createUser(userDto);
            redirectAttributes.addFlashAttribute("message", "Successful registration.");
            return "redirect:/";
        } catch (InvalidInputException | DuplicateEntityException e) {
            model.addAttribute("exceptionMessage", e.getMessage());
            return "register";
        }
    }


    @PostMapping("/search")
    public String process(Model model, @RequestParam String sourceText) {
        model.addAttribute("users", userService.getUsersBySubstring(sourceText));
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("exceptionMessage", "");
        return "register";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleMissingEntity() {
        return "notFound";
    }
}
