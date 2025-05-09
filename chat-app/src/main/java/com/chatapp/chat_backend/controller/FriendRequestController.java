package com.chatapp.chat_backend.controller;

import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.repository.UserRepository;
import com.chatapp.chat_backend.service.FriendRequestService;
import com.chatapp.chat_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private UserRepository userRepository; // eksikti

    @PostMapping("/request")
    public String sendFriendRequest(@RequestParam String toUsername, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = authentication.getName();

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Gönderen kullanıcı bulunamadı"));

        User receiver = userRepository.findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("Alıcı kullanıcı bulunamadı"));

        // İsteği service üzerinden gönderiyoruz
        friendRequestService.sendFriendRequest(sender, receiver);

        redirectAttributes.addFlashAttribute("message", "✅ Arkadaşlık isteği gönderildi!");

        return "redirect:/users";
    }

    @GetMapping("/pending")
    public String viewPendingRequests(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Sadece status = "PENDING" olanları al
        List<FriendRequest> pendingRequests = friendRequestService.findPendingForUser(currentUser);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("currentUser", currentUser);
        return "pending-requests"; // pending-requests.html
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
        redirectAttributes.addFlashAttribute("message", "Arkadaşlıktan çıkarıldı.");
        return "redirect:/friends/list";
    }
}
