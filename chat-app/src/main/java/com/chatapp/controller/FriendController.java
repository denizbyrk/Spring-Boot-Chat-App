package com.chatapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.chatapp.model.FriendRequest;
import com.chatapp.model.User;
import com.chatapp.service.FriendRequestService;
import com.chatapp.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/friends")
public class FriendController {

    private final UserService userService;

    private final FriendRequestService friendRequestService;
    
    public FriendController(UserService userService, FriendRequestService friendRequestService) {
    	
    	this.userService = userService;
    	this.friendRequestService = friendRequestService;
    }
    
    @GetMapping("/list")
    public String viewFriends(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    	
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<User> friends = userService.findFriends(currentUser);        
        List<User> users = userService.findAllExcludingCurrent(currentUser.getId());
        List<User> pendingRequests = friendRequestService.getSentPendingRequests(currentUser)
                                                        .stream()
                                                        .map(FriendRequest::getReceiver)
                                                        .toList();

        model.addAttribute("users", users);
        model.addAttribute("friends", friends);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("currentUser", currentUser);

        return "friends";
    }
}