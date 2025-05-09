package com.chatapp.chat_backend.service;

import com.chatapp.chat_backend.model.FriendRequest;
import com.chatapp.chat_backend.model.User;
import com.chatapp.chat_backend.repository.FriendRequestRepository;
import com.chatapp.chat_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository; // ← düzeltildi

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }

    public List<User> findAllExcludingCurrent(Long currentUserId) {
        List<User> allUsers = userRepository.findAll();
        allUsers.removeIf(user -> user.getId().equals(currentUserId));
        return allUsers;
    }

    // ✅ Arkadaş listesini dönen metot
    public List<User> findFriends(User currentUser) {
        List<User> friends = new ArrayList<>();
        List<FriendRequest> sent = friendRequestRepository.findBySenderAndStatus(currentUser, "ACCEPTED");
        List<FriendRequest> received = friendRequestRepository.findByReceiverAndStatus(currentUser, "ACCEPTED");
        sent.forEach(r -> friends.add(r.getReceiver()));
        received.forEach(r -> friends.add(r.getSender()));
        return friends;
    }
    public void removeFriendship(String currentUsername, Long friendId) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Giriş yapan kullanıcı bulunamadı"));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Arkadaş bulunamadı"));

        // Arkadaşlık ilişkisini her iki yönden de kontrol et ve sil
        List<FriendRequest> requests = friendRequestRepository
                .findBySenderAndReceiverOrReceiverAndSender(currentUser, friend, currentUser, friend);

        if (!requests.isEmpty()) {
            friendRequestRepository.deleteAll(requests);
        } else {
            throw new RuntimeException("Arkadaşlık ilişkisi bulunamadı.");
        }
    }

    
}
