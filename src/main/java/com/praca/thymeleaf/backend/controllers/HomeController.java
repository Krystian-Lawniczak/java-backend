package com.praca.thymeleaf.backend.controllers;

import com.praca.thymeleaf.backend.models.User;
import com.praca.thymeleaf.backend.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        User user = new User();
        user.setName("Jan Kowalski");
        user.setEmail("jan.kowalski@example.com");
        userRepository.save(user);

        model.addAttribute("users", userRepository.findAll());
        return "home";
    }
}
