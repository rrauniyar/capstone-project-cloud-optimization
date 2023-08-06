package com.example.springsocial.controller;

import com.example.springsocial.exception.BadRequestException;
import com.example.springsocial.model.Chat;
import com.example.springsocial.model.ChatList;
import com.example.springsocial.payload.ApiResponse;
import com.example.springsocial.payload.ChatListResponse;
import com.example.springsocial.payload.ChatResponse;
import com.example.springsocial.repository.ChatListRepository;
import com.example.springsocial.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3001")
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatListRepository chatListRepository;
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse>storeChat(@RequestBody ChatResponse chatResponse){

        Optional<Chat> optionalChat = chatRepository.findById(chatResponse.getId());

        if (optionalChat.isPresent()) {
            Chat chat = optionalChat.get();
            System.out.println(chatResponse);

            // Update the fields of the existing Chat object
            chat.setServerResponse(chat.getServerResponse()+chatResponse.getServerResponse());
            chat.setUserInput(chat.getUserInput()+chatResponse.getUserInput());

            // Save the updated Chat object back to the database
            Chat result = chatRepository.save(chat);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/chat") // Specify the path to the endpoint for retrieving a chat by ID
                    .buildAndExpand(result.getId()) // Set the ID of the updated chat as the path variable
                    .toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Chat successfully updated in the DB"));
        } else {
            // Handle the case where the Chat object with the given ID is not found
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/new-chat")
    public ResponseEntity<ApiResponse>createChat(@RequestBody ChatResponse chatResponse) {
        Optional<Chat> optionalChat = chatRepository.findById(chatResponse.getId());
        if (!optionalChat.isPresent()) {


            Chat chat = new Chat();
            System.out.println(chatResponse);
            chat.setServerResponse(chatResponse.getServerResponse());
            chat.setUserInput(chatResponse.getUserInput());
            chat.setId(chatResponse.getId());
            Chat result = chatRepository.save(chat);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/chat") // Specify the path to the endpoint for retrieving a chat by ID
                    .buildAndExpand(result.getId()) // Set the ID of the newly created chat as the path variable
                    .toUri();


            return ResponseEntity.created(location)
                    .body(new ApiResponse(true, "New Chat created"));
        }
        else{
            throw new BadRequestException("Chat Already created");
        }
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<ChatResponse> getChatById(@PathVariable("id") Long id) {
        Optional<Chat> chatOptional = chatRepository.findById(id);

        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setId(chat.getId());
            chatResponse.setServerResponse(chat.getServerResponse());
            chatResponse.setUserInput(chat.getUserInput());

            return ResponseEntity.ok(chatResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all-chats")
    public ResponseEntity<ApiResponse>renderChatList(){
        List<ChatList> chatList = chatListRepository.findAll();
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setData(chatList);

        // Return the response
        return ResponseEntity.ok(response);
    }


    @PostMapping("/create-chat")
    public ResponseEntity<ApiResponse>createChat(@RequestBody  ChatListResponse chatListResponse){
        ChatList chatList=new ChatList();
        chatList.setChatTitle(chatListResponse.getChatTitle());
        ChatList result=chatListRepository.save(chatList);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/create-chat")
                .buildAndExpand(result.getId())
                .toUri();


        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "New Chat created", result.getId()));

    }

}
