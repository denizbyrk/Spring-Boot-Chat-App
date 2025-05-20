package com.chatapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.repository.MessageRepository;

import java.util.List;
import java.util.Comparator;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(User sender, User receiver, String content) {
    	
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        return messageRepository.save(message);
    }

    public List<Message> getMessagesBetween(User sender, User receiver) {
    	
        List<Message> messages = messageRepository.findBySenderAndReceiver(sender, receiver);
        messages.addAll(messageRepository.findBySenderAndReceiver(receiver, sender));
        messages.sort(Comparator.comparing(Message::getTimestamp));
        
        return messages;
    }
}