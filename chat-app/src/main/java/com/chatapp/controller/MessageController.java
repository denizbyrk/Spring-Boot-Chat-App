package com.chatapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class MessageController {

    private final MessageService messageService;

    private final UserService userService;
    
    public MessageController(MessageService messageService, UserService userService) {
    	
    	this.messageService = messageService;
    	this.userService = userService;
    }

    @GetMapping
    public String chatPage(@RequestParam("with") Long withUserId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {

        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User otherUser = userService.findById(withUserId);

        List<Message> messages = messageService.getMessagesBetween(currentUser, otherUser);

        model.addAttribute("messages", messages);
        model.addAttribute("receiver", otherUser);
        model.addAttribute("currentUser", currentUser);

        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("receiverId") Long receiverId,
                               @RequestParam("content") String content,
                               @AuthenticationPrincipal UserDetails userDetails) {

        User sender = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User receiver = userService.findById(receiverId);

        messageService.sendMessage(sender, receiver, content);

        return "redirect:/chat?with=" + receiverId;
    }
    
    @GetMapping("/messages/{receiverId}")
    @ResponseBody
    public List<Message> getMessagesJson(@PathVariable Long receiverId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
    	
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User receiver = userService.findById(receiverId);

        return messageService.getMessagesBetween(currentUser, receiver);
    }
  
}