package com.chatapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chatapp.model.FriendRequest;
import com.chatapp.model.User;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
	
    public List<FriendRequest> findByReceiver(User receiver);
    
    public List<FriendRequest> findBySenderAndStatus(User sender, String status);
    
    public List<FriendRequest> findByReceiverAndStatus(User receiver, String status);
    
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
    	   "(fr.sender = :user1 AND fr.receiver = :user2) OR " +
    	   "(fr.sender = :user2 AND fr.receiver = :user1)")
    public List<FriendRequest> findBySenderAndReceiverOrReceiverAndSender(
    	        @Param("user1") User user1,
    	        @Param("user2") User user2,
    	        @Param("user2") User user2b,
    	        @Param("user1") User user1b);
    
    public void deleteAllBySender(User sender);
    
    public void deleteAllByReceiver(User receiver);
    
    public boolean existsBySenderAndReceiverAndStatusNot(User sender, User receiver, String status);
}