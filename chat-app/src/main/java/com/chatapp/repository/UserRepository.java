package com.chatapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatapp.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	
    Optional<User> findByUsername(String username);
}