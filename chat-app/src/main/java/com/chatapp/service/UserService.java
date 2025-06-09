package com.chatapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatapp.model.FriendRequest;
import com.chatapp.model.User;
import com.chatapp.repository.FriendRequestRepository;
import com.chatapp.repository.MessageRepository;
import com.chatapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    
    private final FriendRequestRepository friendRequestRepository;

    private final MessageRepository messageRepository;
    
    public UserService(UserRepository userRepository, FriendRequestRepository friendRequestRepository, MessageRepository messageRepository) {
    	
    	this.userRepository = userRepository;
    	this.friendRequestRepository = friendRequestRepository;
    	this.messageRepository = messageRepository;
    }
    
    public Optional<User> findByUsername(String username) {
    	
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
    	
        return userRepository.findByEmail(email);
    }
    
    public User save(User user) {
    	
        return userRepository.save(user);
    }
    
    public User findById(Long id) {
    	
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public List<User> findAllExcludingCurrent(Long currentUserId) {
    	
        List<User> allUsers = userRepository.findAll();
        allUsers.removeIf(user -> user.getId().equals(currentUserId));
        
        return allUsers;
    }
    
    public List<User> findFriends(User currentUser) {
    	
        List<User> friends = new ArrayList<>();
        List<FriendRequest> sent = friendRequestRepository.findBySenderAndStatus(currentUser, "ACCEPTED");
        List<FriendRequest> received = friendRequestRepository.findByReceiverAndStatus(currentUser, "ACCEPTED");
        
        sent.forEach(r -> friends.add(r.getReceiver()));
        received.forEach(r -> friends.add(r.getSender()));
        
        return friends;
    }
    
    public void removeFriendship(String currentUsername, Long friendId) {
    	
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Logged in user not found."));
        
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found."));
        
        List<FriendRequest> requests = friendRequestRepository
                .findBySenderAndReceiverOrReceiverAndSender(currentUser, friend, currentUser, friend);
        
        if (!requests.isEmpty()) {
        	
            friendRequestRepository.deleteAll(requests);
        } else {
        	
            throw new RuntimeException("Friendship not found.");
        }
    }
    
    @Transactional
    public void delete(User user) {
    	
        try {

            messageRepository.deleteAllByUserId(user.getId());
            
            friendRequestRepository.deleteAllBySender(user);
            friendRequestRepository.deleteAllByReceiver(user);
            
            userRepository.delete(user);
            
        } catch (Exception e) {
        	
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }
    
    public String getDeletionInfo(User user) {
    	
        int totalMessages = messageRepository.findAllByUser(user).size();
        int sentMessages = messageRepository.findAllBySender(user).size();
        int receivedMessages = messageRepository.findAllByReceiver(user).size();
        
        return String.format("%d Messages to be deleted (%d sent, %d received)", 
                           totalMessages, sentMessages, receivedMessages);
    }
}