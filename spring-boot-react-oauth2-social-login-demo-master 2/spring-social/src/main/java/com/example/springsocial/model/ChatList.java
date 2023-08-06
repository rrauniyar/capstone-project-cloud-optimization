package com.example.springsocial.model;

import javax.persistence.*;

@Entity
@Table(name = "chatList", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class ChatList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chatTitle;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }
}
