package com.example.springsocial.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ChatResponse{


   private String userInput;


   private String serverResponse;

   private Long id;


    public String getUserInput() {
        return userInput;
    }
    public void setUserInput(String userInput){
        this.userInput=userInput;
    }
    public void setServerResponse(String  serverResponse){
        this.serverResponse=serverResponse;
    }
    public String getServerResponse() {
        return serverResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
