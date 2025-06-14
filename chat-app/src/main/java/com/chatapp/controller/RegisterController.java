package com.chatapp.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;

@Controller
public class RegisterController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    
    public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    	
    	this.userRepository = userRepository;
    	this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
    	
        model.addAttribute("user", new User());
        
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {

        if (userRepository.findByEmail(email).isPresent()) {
        	
            model.addAttribute("error", "This email is already in use.");
            model.addAttribute("user", new User());
            
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return "redirect:/login";
    }
}