package com.chatapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;

@Controller
public class WebSocketMessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat")
    public Message handleMessage(Message message) {

        User sender = userService.findById(message.getSender().getId());
        User receiver = userService.findById(message.getReceiver().getId());

        messageService.sendMessage(sender, receiver, message.getContent());

        return message;
    }
}