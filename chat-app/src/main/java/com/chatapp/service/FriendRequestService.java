package com.chatapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatapp.model.FriendRequest;
import com.chatapp.model.User;
import com.chatapp.repository.FriendRequestRepository;

import java.util.List;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public FriendRequest sendFriendRequest(User sender, User receiver) {
    	
        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status("PENDING")
                .build();
        
        return friendRequestRepository.save(request);
    }

    public List<FriendRequest> getReceivedRequests(User receiver) {
    	
        return friendRequestRepository.findByReceiver(receiver);
    }

    public List<FriendRequest> getAcceptedFriends(User user) {
    	
        List<FriendRequest> sent = friendRequestRepository.findBySenderAndStatus(user, "ACCEPTED");
        List<FriendRequest> received = friendRequestRepository.findByReceiverAndStatus(user, "ACCEPTED");
        sent.addAll(received);
        
        return sent;
    }

    public FriendRequest findById(Long id) {
    	
        return friendRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
    }

    public void acceptRequest(FriendRequest request) {
    	
        request.setStatus("ACCEPTED");
        friendRequestRepository.save(request);
    }

    public void declineRequest(FriendRequest request) {
    	
        friendRequestRepository.delete(request);
    }
    
    public List<FriendRequest> getSentPendingRequests(User sender) {
    	
        return friendRequestRepository.findBySenderAndStatus(sender, "PENDING");
    }
    
    public List<FriendRequest> findPendingForUser(User receiver) {
    	
        return friendRequestRepository.findByReceiverAndStatus(receiver, "PENDING");
    }
}