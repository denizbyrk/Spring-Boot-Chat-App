package com.chatapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
	
    public List<Message> findBySenderAndReceiver(User sender, User receiver);
    
    public List<Message> findAllBySender(User sender);
    
    public List<Message> findAllByReceiver(User receiver);
    
    @Query("SELECT m FROM Message m WHERE m.sender = :user OR m.receiver = :user")
    public List<Message> findAllByUser(@Param("user") User user);
    
    @Modifying
    @Query("DELETE FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    public void deleteAllByUserId(@Param("userId") Long userId);
    
    public Optional<Message> findTopBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampDesc(
            Long senderId1, Long receiverId1, Long receiverId2, Long senderId2);
}