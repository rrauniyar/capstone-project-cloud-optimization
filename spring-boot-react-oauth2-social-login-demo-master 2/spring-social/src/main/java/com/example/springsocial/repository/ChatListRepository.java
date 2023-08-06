package com.example.springsocial.repository;

import com.example.springsocial.model.ChatList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatListRepository extends JpaRepository<ChatList,Long> {
}
