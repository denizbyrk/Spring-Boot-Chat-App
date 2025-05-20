package com.chatapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
	
    List<Message> findBySenderAndReceiver(User sender, User receiver);
}