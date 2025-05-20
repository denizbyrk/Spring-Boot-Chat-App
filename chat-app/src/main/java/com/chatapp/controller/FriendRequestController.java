package com.chatapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chatapp.model.FriendRequest;
import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;
import com.chatapp.service.FriendRequestService;
import com.chatapp.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/friends")
public class FriendRequestController {

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/request")
    public String sendFriendRequest(@RequestParam String toUsername, RedirectAttributes redirectAttributes) {
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = authentication.getName();

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        friendRequestService.sendFriendRequest(sender, receiver);

        redirectAttributes.addFlashAttribute("message", "✅ Friend request sent!");

        return "redirect:/users";
    }

    @GetMapping("/pending")
    public String viewPendingRequests(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        List<FriendRequest> pendingRequests = friendRequestService.findPendingForUser(currentUser);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("currentUser", currentUser);
        
        return "pending-requests";
    }


    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam("requestId") Long requestId) {
    	
        FriendRequest request = friendRequestService.findById(requestId);
        friendRequestService.acceptRequest(request);
        
        return "redirect:/friends/pending";
    }

    @PostMapping("/decline")
    public String declineFriendRequest(@RequestParam("requestId") Long requestId) {
    	
        FriendRequest request = friendRequestService.findById(requestId);
        friendRequestService.declineRequest(request);
        
        return "redirect:/friends/pending";
    }
    
    @PostMapping("/remove")
    public String removeFriend(@RequestParam("friendId") Long friendId,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
    	
        String currentUsername = principal.getName();
        userService.removeFriendship(currentUsername, friendId);
        redirectAttributes.addFlashAttribute("message", "Removed from friend list.");
        
        return "redirect:/friends/list";
    }
}