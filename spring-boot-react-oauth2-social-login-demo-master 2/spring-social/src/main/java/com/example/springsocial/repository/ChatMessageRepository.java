package com.example.springsocial.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springsocial.model.ChatMessage;



@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long>{
    
}

