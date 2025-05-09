package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/friends")
public class FriendController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String viewFriends(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<User> friends = userService.findFriends(currentUser);
        model.addAttribute("friends", friends);
        model.addAttribute("currentUser", currentUser);
        return "friends"; // src/main/resources/templates/friends.html olmalı
    }
}
