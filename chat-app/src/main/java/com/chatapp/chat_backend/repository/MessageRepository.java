package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.model.Message;
import com.chatapp.chat_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiver(User sender, User receiver);
}
