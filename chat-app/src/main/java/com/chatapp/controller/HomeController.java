package com.chatapp.controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chatapp.model.FriendRequest;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.repository.MessageRepository;
import com.chatapp.service.FriendRequestService;
import com.chatapp.service.UserService;

@Controller
@RequestMapping("/messages")
public class HomeController {

    private final UserService userService;

    private final FriendRequestService friendRequestService;

    private final MessageRepository messageRepository;

    public HomeController(UserService userService, FriendRequestService friendRequestService, MessageRepository messageRepository) {
    	
    	this.userService = userService;
    	this.friendRequestService = friendRequestService;
    	this.messageRepository = messageRepository;
    }

    @GetMapping
    public String listUsers(Model model, @AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(name = "search", required = false) String search) {

        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")); 

        List<User> users = userService.findAllExcludingCurrent(currentUser.getId());

        if (search != null && !search.isEmpty()) {
        	
            users = users.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        List<User> friends = userService.findFriends(currentUser);
        List<User> pendingRequests = friendRequestService.getSentPendingRequests(currentUser)
                                                        .stream()
                                                        .map(FriendRequest::getReceiver)
                                                        .toList();

        model.addAttribute("users", users);
        model.addAttribute("friends", friends);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("currentUser", currentUser);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        Map<Long, Message> lastMessages = new HashMap<>();
        Map<Long, String> lastMessageTimes = new HashMap<>();

        for (User friend : friends) {
            messageRepository
                .findTopBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampDesc(
                        currentUser.getId(), friend.getId(),
                        currentUser.getId(), friend.getId())
                .ifPresent(msg -> {
                    lastMessages.put(friend.getId(), msg);
                    lastMessageTimes.put(friend.getId(), msg.getTimestamp().format(formatter));
                });
        }

        model.addAttribute("lastMessages", lastMessages);
        model.addAttribute("lastMessageTimes", lastMessageTimes);
           
        return "messages";
    }
}