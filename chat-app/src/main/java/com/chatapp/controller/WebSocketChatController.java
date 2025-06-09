package com.chatapp.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;

@Controller
public class WebSocketChatController {

    private final MessageService messageService;

    private final UserService userService;

    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketChatController(MessageService messageService, UserService userService, SimpMessagingTemplate messagingTemplate) {
    	
    	this.messageService = messageService;
    	this.userService = userService;
    	this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void send(Message message, Principal principal) {

        User sender;
        
        if (principal != null) {

            sender = userService.findByUsername(principal.getName())
                    .orElse(null);
        } else {
            sender = null;
        }
        
        if (sender == null && message.getSender() != null && message.getSender().getId() != null) {
            sender = userService.findById(message.getSender().getId());
        }
        
        if (sender == null) {
            throw new RuntimeException("Cannot identify sender");
        }
        
        User receiver = userService.findById(message.getReceiver().getId());

        Message saved = messageService.sendMessage(sender, receiver, message.getContent());
        saved.setSender(sender);
        saved.setReceiver(receiver);

        String chatRoom = "chat-" + Math.min(sender.getId(), receiver.getId()) + "-" + Math.max(sender.getId(), receiver.getId());
        
        messagingTemplate.convertAndSend("/topic/" + chatRoom, saved);
    }
}