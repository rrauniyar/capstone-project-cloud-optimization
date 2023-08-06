package com.capstone.beginsetup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import jakarta.persistence.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean checker;

    private String message;
   
    private ZonedDateTime timeStamp;
    
    // Getters and setters
}
