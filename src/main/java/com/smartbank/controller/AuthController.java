package com.smartbank.controller;

import com.smartbank.dto.UserRegistrationDto;
import com.smartbank.entity.User;
import com.smartbank.service.UserService;
import com.smartbank.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;

import java.math.BigDecimal;

@Controller
@RequestMapping("")
public class AuthController {

    private final UserService userService;
    private final AccountService accountService;

    public AuthController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto dto, 
                              BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("user", dto);
                return "register";
            }

            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                model.addAttribute("error", "Passwords do not match");
                model.addAttribute("user", dto);
                return "register";
            }

            User user = userService.registerUser(dto);
            
            // Create default savings account
            accountService.createAccount(user, "SAVINGS", new BigDecimal("1000.00"));

            model.addAttribute("success", "Registration successful! Please login.");
            model.addAttribute("user", new UserRegistrationDto());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", dto);
            return "register";
        }
    }

}
