package com.example.springsocial.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


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

    @ManyToOne(cascade = CascadeType.MERGE)
@JoinColumn(name = "user_id", referencedColumnName = "id")
private User user;

@JsonIgnore
public void setUserId(Long id) {
    user.setId(id);
}
 
}
