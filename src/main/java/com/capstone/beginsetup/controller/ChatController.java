// package com.capstone.beginsetup.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.capstone.beginsetup.model.ChatMessage;
// import com.capstone.beginsetup.repository.ChatMessageRepository;

// @RestController
// @RequestMapping("/chat")
// public class ChatController {
//     private final ChatMessageRepository chatMessageRepository;
//     @Autowired
//     public ChatController(ChatMessageRepository chatMessageRepository) {
//         this.chatMessageRepository = chatMessageRepository;
//     }
//      @GetMapping("/messages")
//     public List<ChatMessage> getChatMessages() {
//         return chatMessageRepository.findAll();
//     }
//     @PostMapping("/messages")
//     public void saveChatMessage(@RequestBody ChatMessage chatMessage) {
//         chatMessageRepository.save(chatMessage);
//     }
// }

