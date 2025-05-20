package com.chatapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;

@Controller
public class WebSocketChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void send(Message message, Authentication authentication) {
    	
        User sender = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userService.findById(message.getReceiver().getId());

        messageService.sendMessage(sender, receiver, message.getContent());

        message.setSender(sender);
        message.setReceiver(receiver);

        messagingTemplate.convertAndSendToUser(sender.getUsername(), "/queue/messages", message);
        messagingTemplate.convertAndSendToUser(receiver.getUsername(), "/queue/messages", message);
    }
}