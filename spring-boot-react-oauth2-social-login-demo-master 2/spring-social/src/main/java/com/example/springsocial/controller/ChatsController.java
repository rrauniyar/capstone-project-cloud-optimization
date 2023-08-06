package com.example.springsocial.controller;


import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springsocial.model.ChatMessage;
import com.example.springsocial.model.User;
import com.example.springsocial.repository.ChatMessageRepository;
import com.example.springsocial.repository.UserRepository;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;



@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:3001")
public class ChatsController {

    @Autowired
    private UserRepository userRepository;

    private final ChatMessageRepository chatMessageRepository;
    @Autowired
    public ChatsController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }
    //  @GetMapping("/messages")
    // public Optional<ChatMessage> getChatMessagesByUserId(HttpServletRequest request) {
    //     Long id=(Long) request.getAttribute("id");
    //     return chatMessageRepository.findById(id);
    // }
         @GetMapping("/messages")
    public List<ChatMessage> getChatMessagesByUserId() {
        //Long id=(Long) request.getAttribute("id");
        return chatMessageRepository.findAll();
    }
    @PostMapping("/messages")
    public void saveChatMessage(@RequestBody ChatMessage chatMessage, HttpServletRequest request) {
        Long id=(Long) request.getAttribute("id");
System.out.println("Message controller : "+id);
        User user= userRepository.getById(id);

        chatMessage.setUser(user);
        System.out.println("user saved: "+user);
        chatMessageRepository.save(chatMessage);
        System.out.println("Printed");
    }
    // @PostMapping("/messages")
    // public void saveChatMessage(@RequestBody ChatMessage chatMessage) {
    


      
    //     chatMessageRepository.save(chatMessage);
  
    // }
    
    
}

