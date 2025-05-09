package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.service.FriendRequestService;
import com.chatapp.chat_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRequestService friendRequestService;

    @GetMapping
    public String listUsers(Model model, @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(name = "search", required = false) String search) {

        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<User> users = userService.findAllExcludingCurrent(currentUser.getId());

        // Arama filtresi
        if (search != null && !search.isEmpty()) {
            users = users.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        // Zaten arkadaş olunanlar ve istek gönderilmiş kullanıcılar
        List<User> friends = userService.findFriends(currentUser);
        List<User> pendingRequests = friendRequestService.getSentPendingRequests(currentUser)
                                                        .stream()
                                                        .map(FriendRequest::getReceiver)
                                                        .toList();

        model.addAttribute("users", users);
        model.addAttribute("friends", friends);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("currentUser", currentUser);

        return "users";
    }
    

}
