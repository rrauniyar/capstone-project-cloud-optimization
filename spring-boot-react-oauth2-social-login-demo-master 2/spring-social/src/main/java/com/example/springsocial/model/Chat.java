package com.example.springsocial.model;

import javax.persistence.*;

@Entity
@Table(name = "chat", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userInput;
    private String serverResponse;

    public void setId(Long id){
        this.id=id;
    }
    public Long getId() {
        return id;
    }

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
}
